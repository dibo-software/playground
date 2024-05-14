<script setup lang="ts">
const props = defineProps<{
  position: 'left' | 'right'
  message: string
}>()

const classNames = computed(() => ['chat-item', `chat-item-${props.position}`].join(' '))
</script>

<template>
  <div :class="classNames">
    <div class="chat-icon">{{ position === 'left' ? 'A' : 'Q' }}</div>
    <div class="chat-message" v-html="message" />
  </div>
</template>

<style scoped lang="scss">
.chat-item {
  display: flex;
  margin: 20px 5px 10px;
  border-radius: 5px;
  padding: 5px;
  line-height: 30px;
  color: var(--el-text-color);
  font-size: 15px;
  position: relative;
  .chat-icon {
    display: flex;
    box-sizing: border-box;
    width: 30px;
    height: 30px;
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
    border-radius: 50%;
    justify-content: center;
    align-items: center;
    padding: 22px;
  }
  .chat-message {
    background-color: var(--el-color-info-light-9);
    margin: 0 10px;
    padding: 0 10px;
    box-sizing: border-box;
    border-radius: 10px;
    overflow: auto;
    display: flex;
    align-items: center;
  }
  &-right {
    flex-direction: row-reverse;
    .chat-icon {
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }
  }
}
</style>
