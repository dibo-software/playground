<script setup lang="ts" name="Message">
import { Search } from '@element-plus/icons-vue'
import type { Message } from './type'
import Detail from '@/views/system/message/Detail.vue'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter } = useList<Message>({
  baseApi: '/message'
})
getList()

const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const { relatedData, initRelatedData } = useOption({ dict: ['MESSAGE_CHANNEL', 'MESSAGE_STATUS'] })
initRelatedData()
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-space>
        <el-input v-model="queryParam.title" clearable placeholder="消息标题" @change="onSearch" />
        <el-select v-model="queryParam.channel" placeholder="请选择发送通道" clearable @change="onSearch">
          <el-option
            v-for="item in relatedData.messageChannelOptions || []"
            :key="item.value"
            :value="item.value"
            :label="item.label"
          />
        </el-select>
        <el-select v-model="queryParam.status" clearable placeholder="请选择消息状态" @change="onSearch">
          <el-option
            v-for="item in relatedData.messageStatusOptions || []"
            :key="item.value"
            :value="item.value"
            :label="item.label"
          />
        </el-select>
        <el-date-picker
          v-model="queryParam.createTime"
          placeholder="发送日期"
          clearable
          type="date"
          value-format="YYYY-MM-DD"
          @change="onSearch"
        />
        <el-button :icon="Search" type="primary" @click="onSearch">查询</el-button>
        <el-button title="重置搜索条件" @click="resetFilter">重置</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" height="100%">
      <el-table-column prop="businessType" label="业务类型" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="senderName" label="发送方" />
      <el-table-column prop="receiverName" label="接收方" />
      <el-table-column prop="channelLabel" label="发送通道">
        <template #default="{ row }">
          <el-tag :color="row.channelLabel?.ext?.color" effect="dark" type="info">
            {{ row.channelLabel?.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="statusLabel" label="发送状态">
        <template #default="{ row }">
          <el-tag :color="row.statusLabel?.ext?.color" effect="dark" type="info">
            {{ row.statusLabel?.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="165" />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button v-has-permission="'detail'" text bg type="primary" size="small" @click="openDetail(row.id)">
            {{ $t('operation.detail') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="pagination.total"
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 15, 20, 30, 50, 100]"
      small
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="getList()"
      @current-change="getList()"
    />

    <Detail ref="detailRef" />
  </div>
</template>

<style scoped></style>
