<script setup lang="ts" name="ScheduleJob">
import { Plus, More, CaretRight } from '@element-plus/icons-vue'
import type { ScheduleJob } from './type'
import Form from './Form.vue'
import LogList from './log/List.vue'

const baseApi = '/schedule-job'

const { getList, loading, dataList, remove } = useList<ScheduleJob>({ baseApi })

getList()

const executeOnce = (id: string) => {
  api
    .put(`${baseApi}/execute-once/${id}`)
    .then(res => {
      ElMessage.success(res.msg)
    })
    .catch(err => {
      ElMessage.error(err.msg || err.message || '稍后重试')
    })
}

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const logListRef = ref()
const openLog = (id: string) => {
  logListRef.value?.open(id)
}

const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')
const logListPermission = checkPermission('logList')
</script>

<template>
  <el-scrollbar class="content">
    <el-row v-loading="loading">
      <el-col v-for="item in dataList" :key="item.id" :xl="6" :lg="6" :md="8" :sm="12" :xs="24">
        <el-card shadow="hover">
          <el-descriptions :column="1" :title="item.jobName" style="zoom: 0.85">
            <el-descriptions-item label="执行类">
              {{ item.jobKey }}
            </el-descriptions-item>
            <el-descriptions-item label="定时规则">
              {{ item.cron }}
            </el-descriptions-item>
            <el-descriptions-item label="执行策略">
              {{ item.initStrategyLabel }}
            </el-descriptions-item>
            <el-descriptions-item label="日志状态">
              <el-tag v-if="item.saveLog"> 启用</el-tag>
              <el-tag v-else type="info"> 停用</el-tag>
            </el-descriptions-item>
          </el-descriptions>
          <div class="bottom">
            <el-tag v-if="item.jobStatus === 'A'"> 准备就绪</el-tag>
            <el-tag v-else type="info"> 停用</el-tag>

            <div style="margin-left: auto">
              <el-popconfirm title="确认立即执行一次吗?" @confirm="executeOnce(item.id)">
                <template #reference>
                  <el-button v-has-permission="'executeOnce'" circle :icon="CaretRight" type="primary" />
                </template>
              </el-popconfirm>

              <el-dropdown
                v-has-permission="['update', 'delete', 'logList']"
                :hide-on-click="false"
                style="margin-left: 10px"
              >
                <el-button circle :icon="More" type="primary" plain />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="updatePermission" @click="openForm(item.id)">
                      {{ $t('operation.update') }}
                    </el-dropdown-item>
                    <el-dropdown-item v-if="logListPermission" @click="openLog(item.id)"> 日志 </el-dropdown-item>
                    <el-dropdown-item v-if="deletePermission" divided @click="remove(item.id)">
                      {{ $t('operation.delete') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col v-has-permission="'create'" :xl="6" :lg="6" :md="8" :sm="12" :xs="24">
        <el-card shadow="hover" class="add" @click="openForm()">
          <el-icon :size="25">
            <Plus />
          </el-icon>
          <p>{{ $t('operation.create') }}</p>
        </el-card>
      </el-col>
    </el-row>

    <Form ref="formRef" @complete="getList" />
    <log-list ref="logListRef" />
  </el-scrollbar>
</template>

<style scoped lang="scss">
.content {
  height: 100%;
  background-color: var(--el-fill-color-light);

  .el-row {
    margin: 0 !important;

    .el-col {
      padding: 6px;
    }
  }
}

.bottom {
  display: flex;
  align-items: center;
  padding-top: 10px;
  margin-bottom: -8px;
  border-top: 1px solid var(--el-color-info-light-7);
}

.add {
  height: 100%;
  min-height: 220px;
  cursor: pointer;

  :deep(.el-card__body) {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-secondary);
    height: calc(100% - var(--el-card-padding) * 2);

    &:hover {
      color: var(--el-color-primary);
    }
  }
}
</style>
