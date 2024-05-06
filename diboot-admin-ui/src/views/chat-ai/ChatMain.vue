<script setup lang="ts">
import { ArrowDown, Position } from '@element-plus/icons-vue'
import ChatItem from './ChatItem.vue'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

import useChatAiStore from '@/store/chat-ai'
import auth from '@/utils/auth'
import { api } from '@/utils/request'
type RoleType = 'system' | 'user' | 'assistant'
const chatAiStore = useChatAiStore()
const { currentMessages, currentSession } = storeToRefs(chatAiStore)

const inputMessage = ref('')
// 模型
const currentModel = ref<string>('qwen-turbo')
const modelOptions: LabelValue[] = [
  { label: 'qwen-turbo（通义千问）', value: 'qwen-turbo' },
  { label: 'qwen-plus（通义千问）', value: 'qwen-plus' },
  { label: 'qwen-max（通义千问）', value: 'qwen-max' }
]
// 切换模型
const handleCommand = (val: string) => (currentModel.value = val)

/**
 * 动态计算正在使用的模型
 */
const useModel = computed(() => {
  const model = modelOptions.filter(item => item.value === currentModel.value)
  return model ? model[0].label : 'qwen-turbo（通义千问）'
})
//创建新消息
const createNewMessage = (content: string, role?: RoleType) => {
  return { role, content }
}
// 发送消息
const sendMessage = async (message: string, model: string) => {
  inputMessage.value = undefined
  await chatAiStore.beforeSendMessage(message)
  const newMessage = createNewMessage(message, 'user')
  currentMessages.value.push(newMessage)
  //  截取最近3个发送到后端
  const cloneMessages = _.cloneDeep(currentMessages.value)
  // 添加一个空系统消息占位
  const systemMessage = createNewMessage('')
  currentMessages.value.push(systemMessage)

  const controller = new AbortController()
  const signal = controller.signal
  fetchEventSource('/api/ai-session-record/chat', {
    signal, // 传递信号以便可以中断请求
    openWhenHidden: true,
    method: 'POST',
    headers: {
      Authorization: auth.getToken() as string,
      'Content-Type': 'application/json;charset=utf-8'
    },
    body: JSON.stringify({
      // 截取最近3个发送到后端
      messages: cloneMessages.length >= 3 ? cloneMessages.slice(-3) : cloneMessages,
      model
    }),
    onmessage(ev) {
      const { choices } = JSON.parse(ev.data)
      const choice = choices[0]
      const answerMessage = currentMessages.value[currentMessages.value.length - 1]
      answerMessage.role = choice.message.role
      answerMessage.content = marked.parse(choice.message.content, {
        highlight: function (code, lang) {
          const language = hljs.getLanguage(lang) ? lang : 'plaintext'
          return hljs.highlight(code, { language }).value
        }
      })
      if (choice.finishReason === 'stop') {
        // 含有代码，最后通义刷新高亮
        const blocks = document.querySelectorAll('pre code')
        blocks.forEach(block => {
          hljs.highlightBlock(block)
        })
        // 消息接收成功，消息存储至数据库
        api.post<boolean>(`/ai-session-record`, {
          sessionId: currentSession.value.id,
          model,
          requestBody: JSON.stringify(newMessage),
          responseBody: JSON.stringify(answerMessage)
        })
        // 如果响应结束，关闭请求
        controller.abort()
      }
    }
  })
}
</script>

<template>
  <el-main class="chat-ai-main">
    <el-dropdown class="custom-dropdown" @command="handleCommand">
      <span style="display: flex">
        {{ useModel }}
        <el-icon>
          <arrow-down />
        </el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="(model, index) in modelOptions"
            :key="`${model.value}_${index}`"
            :command="model.value"
            >{{ model.label }}</el-dropdown-item
          >
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <el-scrollbar>
      <template v-if="currentMessages.length > 0">
        <chat-item
          v-for="(message, index) in currentMessages"
          :key="`message_${index}`"
          :position="message.role === 'user' ? 'right' : 'left'"
          :message="message.content"
        />
      </template>
      <template v-else>
        <el-empty description="无记录" />
      </template>
    </el-scrollbar>
    <div class="chat-input">
      <el-input
        ref="inputRef"
        v-model="inputMessage"
        :rows="4"
        type="textarea"
        placeholder="请输入您的问题"
        resize="none"
      />
      <div class="chat-tools">
        <el-button
          size="small"
          :icon="Position"
          @click="sendMessage(inputMessage, currentModel)"
          @keydown.ctrl.enter="sendMessage(inputMessage, currentModel)"
          >发送（ctrl + enter）</el-button
        >
      </div>
    </div>
  </el-main>
</template>
<style scoped lang="scss">
.chat-ai-main {
  height: 100%;
  padding: 0;
  :deep(.el-scrollbar) {
    height: calc(100% - 100px - 40px);
  }
  .chat-input {
    position: relative;

    :deep(.el-textarea__inner) {
      padding-bottom: 20px;
    }

    .chat-tools {
      position: absolute;
      bottom: 3px;
      width: 100%;
      display: flex;
      justify-content: end;
      align-items: center;
      padding: 0 5px;
      box-sizing: border-box;
    }
  }
}
.custom-dropdown {
  &:focus-visible {
    outline: unset;
  }
}
</style>
