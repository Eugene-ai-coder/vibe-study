<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
    <!-- 툴바: 타이틀 + 컬럼 설정 -->
    <div v-if="title || storageKey" class="flex items-center justify-between px-3 py-1.5 border-b border-gray-100">
      <span v-if="title" class="text-xs font-semibold text-gray-700">{{ title }}</span>
      <span v-else />
      <button
        v-if="storageKey"
        ref="settingsBtnRef"
        @click="openSettings"
        class="p-1 rounded text-gray-400 hover:text-gray-600 hover:bg-gray-100"
        title="컬럼 설정"
      >
        <svg viewBox="0 0 20 20" fill="currentColor" class="w-4 h-4">
          <path fill-rule="evenodd" d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z" clip-rule="evenodd" />
        </svg>
      </button>
      <ColumnSettingsPopup
        v-if="settingsOpen && settingsAnchorRect"
        :columns="columns"
        :column-order="columnOrderState"
        :hidden-columns="hiddenColumns"
        :anchor-rect="settingsAnchorRect"
        @apply="handleSettingsApply"
        @reset="handleSettingsReset"
        @close="settingsOpen = false"
      />
    </div>

    <div class="overflow-x-auto">
      <table :style="{ tableLayout: 'fixed', width: totalWidth + 'px' }" class="text-sm">
        <colgroup>
          <col v-for="col in visibleColumns" :key="col.key" :style="{ width: colWidths[col.key] + 'px' }" />
        </colgroup>

        <!-- 헤더 -->
        <thead class="border-b border-gray-200">
          <tr>
            <th
              v-for="(col, idx) in visibleColumns"
              :key="col.key"
              :draggable="idx >= pinnedCount"
              @dragstart="onDragStart($event, col.key)"
              @dragover.prevent
              @drop="onDrop($event, col.key)"
              :style="{ width: colWidths[col.key] + 'px', minWidth: (col.minSize ?? 40) + 'px' }"
              :class="[
                'relative px-3 py-2 text-left text-xs font-medium text-gray-500 select-none whitespace-nowrap border-r border-gray-200 last:border-r-0',
                idx < pinnedCount ? 'bg-gray-100 sticky z-20' : 'bg-gray-50',
                idx >= pinnedCount ? 'cursor-grab active:cursor-grabbing' : ''
              ]"
            >
              <div class="flex items-center gap-1">
                <div
                  :class="['flex items-center flex-1 min-w-0', col.sortable !== false ? 'cursor-pointer' : '']"
                  @click="col.sortable !== false && toggleSort(col.key)"
                >
                  <span class="truncate">{{ col.header }}</span>
                  <span v-if="col.sortable !== false" class="ml-1">
                    <span v-if="sortKey !== col.key" class="text-gray-300">↕</span>
                    <span v-else class="text-blue-600">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                  </span>
                </div>
                <button
                  v-if="col.filterable !== false"
                  :ref="el => filterBtnRefs[col.key] = el"
                  @click.stop="openFilter(col.key)"
                  class="p-0.5 rounded hover:bg-gray-200"
                  title="필터"
                >
                  <svg viewBox="0 0 16 16" fill="currentColor" :class="['w-3 h-3 shrink-0', columnFilters[col.key] ? 'text-blue-600' : 'text-gray-400 hover:text-gray-600']">
                    <path d="M1 2a1 1 0 011-1h12a1 1 0 01.8 1.6L10 8.5V13a1 1 0 01-.45.83l-2 1.34A1 1 0 016 14.34V8.5L1.2 2.6A1 1 0 011 2z" />
                  </svg>
                </button>
              </div>

              <!-- 리사이즈 핸들 -->
              <div
                @mousedown.prevent="startResize($event, col.key)"
                class="absolute right-0 top-0 h-full w-1.5 cursor-col-resize select-none touch-none hover:bg-blue-400"
              />
            </th>
          </tr>
        </thead>

        <!-- 바디 -->
        <tbody>
          <tr v-if="processedData.length === 0">
            <td :colspan="visibleColumns.length" class="px-4 py-6 text-center text-xs text-gray-400">
              {{ emptyMessage }}
            </td>
          </tr>
          <tr
            v-for="row in processedData"
            :key="row[rowIdAccessor]"
            @click="$emit('rowClick', row)"
            :class="[
              'h-7 border-b border-gray-100 cursor-pointer',
              selectedRowId != null && row[rowIdAccessor] === selectedRowId ? 'bg-blue-50' : 'hover:bg-gray-50'
            ]"
          >
            <td
              v-for="(col, idx) in visibleColumns"
              :key="col.key"
              :class="[
                'px-3 text-xs text-gray-700 truncate border-r border-gray-100 last:border-r-0',
                idx < pinnedCount ? 'sticky z-10 bg-white' : '',
                selectedRowId != null && row[rowIdAccessor] === selectedRowId && idx < pinnedCount ? '!bg-blue-50' : ''
              ]"
            >
              <template v-if="col.cell">
                <component :is="col.cell" :row="row" :value="row[col.key]" />
              </template>
              <template v-else>{{ row[col.key] }}</template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 필터 팝업 -->
    <ColumnFilterPopup
      v-if="filterOpen && filterAnchorRect"
      :column-id="filterColumnId"
      :data="data"
      :current-filter="columnFilters[filterColumnId]"
      :anchor-rect="filterAnchorRect"
      @apply="onFilterApply"
      @close="filterOpen = false"
    />

    <!-- 페이징 -->
    <div v-if="onPageChange" class="flex items-center justify-between px-3 py-2 border-t border-gray-200 bg-gray-50 text-xs text-gray-600">
      <span>총 {{ totalElements }}건 ({{ pageSize }}건씩)</span>
      <div class="flex items-center gap-1">
        <button @click="onPageChange(0)" :disabled="page === 0"
          class="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100">«</button>
        <button @click="onPageChange(page - 1)" :disabled="page === 0"
          class="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100">‹</button>
        <button
          v-for="p in pageButtons"
          :key="p"
          @click="onPageChange(p)"
          :class="[
            'px-2.5 py-1 rounded border',
            p === page ? 'bg-blue-600 text-white border-blue-600' : 'border-gray-300 hover:bg-gray-100'
          ]"
        >{{ p + 1 }}</button>
        <button @click="onPageChange(page + 1)" :disabled="page >= totalPages - 1"
          class="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100">›</button>
        <button @click="onPageChange(totalPages - 1)" :disabled="page >= totalPages - 1"
          class="px-2 py-1 rounded border border-gray-300 disabled:opacity-40 hover:bg-gray-100">»</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import ColumnFilterPopup from './ColumnFilterPopup.vue'
import ColumnSettingsPopup from './ColumnSettingsPopup.vue'

const props = defineProps({
  columns: { type: Array, required: true },
  data: { type: Array, required: true },
  pinnedCount: { type: Number, default: 0 },
  page: { type: Number, default: 0 },
  totalPages: { type: Number, default: 0 },
  totalElements: { type: Number, default: 0 },
  pageSize: { type: Number, default: 10 },
  onPageChange: { type: Function, default: null },
  selectedRowId: { default: null },
  rowIdAccessor: { type: String, default: 'id' },
  emptyMessage: { type: String, default: '데이터가 없습니다.' },
  storageKey: { type: String, default: '' },
  title: { type: String, default: '' },
})

defineEmits(['rowClick'])

/* 컬럼 키 목록 */
const defaultOrder = computed(() => props.columns.map(c => c.accessorKey ?? c.id ?? c.key))

/* localStorage */
function loadSettings() {
  if (!props.storageKey) return null
  try {
    const raw = localStorage.getItem(`datagrid_col_${props.storageKey}`)
    return raw ? JSON.parse(raw) : null
  } catch { return null }
}

function saveSettings(order, hidden) {
  if (!props.storageKey) return
  localStorage.setItem(`datagrid_col_${props.storageKey}`, JSON.stringify({ order, hidden: [...hidden] }))
}

const saved = loadSettings()
const columnOrderState = ref(
  saved?.order
    ? [...saved.order.filter(id => defaultOrder.value.includes(id)), ...defaultOrder.value.filter(id => !saved.order.includes(id))]
    : [...defaultOrder.value]
)
const hiddenColumns = ref(new Set(saved?.hidden ?? []))

/* 가시 컬럼 */
const visibleColumns = computed(() => {
  const colMap = {}
  props.columns.forEach(c => {
    const key = c.accessorKey ?? c.id ?? c.key
    colMap[key] = { ...c, key }
  })
  return columnOrderState.value
    .filter(id => !hiddenColumns.value.has(id) && colMap[id])
    .map(id => colMap[id])
})

/* 컬럼 너비 */
const colWidths = reactive({})
onMounted(() => {
  props.columns.forEach(c => {
    const key = c.accessorKey ?? c.id ?? c.key
    colWidths[key] = c.size ?? 150
  })
})

const totalWidth = computed(() =>
  visibleColumns.value.reduce((sum, col) => sum + (colWidths[col.key] || 150), 0)
)

/* 리사이즈 */
const startResize = (e, colKey) => {
  const startX = e.clientX
  const startWidth = colWidths[colKey] || 150
  const onMove = (ev) => {
    colWidths[colKey] = Math.max(40, startWidth + ev.clientX - startX)
  }
  const onUp = () => {
    document.removeEventListener('mousemove', onMove)
    document.removeEventListener('mouseup', onUp)
  }
  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onUp)
}

/* 정렬 */
const sortKey = ref(null)
const sortDir = ref('asc')

const toggleSort = (key) => {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDir.value = 'asc'
  }
}

/* 필터 */
const columnFilters = reactive({})
const filterOpen = ref(false)
const filterColumnId = ref('')
const filterAnchorRect = ref(null)
const filterBtnRefs = reactive({})

const openFilter = (colKey) => {
  const btn = filterBtnRefs[colKey]
  if (btn) filterAnchorRect.value = btn.getBoundingClientRect()
  filterColumnId.value = colKey
  filterOpen.value = true
}

const onFilterApply = (filterValue) => {
  if (filterValue) columnFilters[filterColumnId.value] = filterValue
  else delete columnFilters[filterColumnId.value]
}

/* 필터 함수 */
function matchFilter(cellRaw, filter) {
  if (!filter) return true
  const { condition, text, checkedValues } = filter
  const cellVal = cellRaw != null ? String(cellRaw) : ''

  let conditionPass = true
  if (condition && condition !== 'contains') {
    switch (condition) {
      case 'equals':     conditionPass = cellVal.toLowerCase() === (text ?? '').toLowerCase(); break
      case 'startsWith': conditionPass = cellVal.toLowerCase().startsWith((text ?? '').toLowerCase()); break
      case 'endsWith':   conditionPass = cellVal.toLowerCase().endsWith((text ?? '').toLowerCase()); break
      case 'isEmpty':    conditionPass = cellVal.trim() === ''; break
      case 'isNotEmpty': conditionPass = cellVal.trim() !== ''; break
    }
  } else if (text) {
    conditionPass = cellVal.toLowerCase().includes(text.toLowerCase())
  }

  let checkPass = true
  if (checkedValues && checkedValues.length > 0) {
    checkPass = checkedValues.includes(cellRaw != null ? String(cellRaw) : '')
  }

  return conditionPass && checkPass
}

/* 최종 데이터: 필터 → 정렬 */
const processedData = computed(() => {
  let result = [...props.data]

  // 필터 적용
  const filterKeys = Object.keys(columnFilters)
  if (filterKeys.length > 0) {
    result = result.filter(row =>
      filterKeys.every(key => matchFilter(row[key], columnFilters[key]))
    )
  }

  // 정렬 적용
  if (sortKey.value) {
    const key = sortKey.value
    const dir = sortDir.value === 'asc' ? 1 : -1
    result.sort((a, b) => {
      const va = a[key] ?? ''
      const vb = b[key] ?? ''
      if (va < vb) return -dir
      if (va > vb) return dir
      return 0
    })
  }

  return result
})

/* 드래그 순서 변경 */
const dragColKey = ref(null)

const onDragStart = (e, colKey) => {
  dragColKey.value = colKey
  e.dataTransfer.effectAllowed = 'move'
}

const onDrop = (e, targetColKey) => {
  e.preventDefault()
  const srcKey = dragColKey.value
  if (!srcKey || srcKey === targetColKey) return
  const arr = [...columnOrderState.value]
  const srcIdx = arr.indexOf(srcKey)
  const tgtIdx = arr.indexOf(targetColKey)
  if (srcIdx === -1 || tgtIdx === -1 || tgtIdx < props.pinnedCount) return
  arr.splice(srcIdx, 1)
  arr.splice(tgtIdx, 0, srcKey)
  columnOrderState.value = arr
  dragColKey.value = null
}

/* 컬럼 설정 팝업 */
const settingsOpen = ref(false)
const settingsAnchorRect = ref(null)
const settingsBtnRef = ref(null)

const openSettings = () => {
  if (!settingsOpen.value && settingsBtnRef.value) {
    settingsAnchorRect.value = settingsBtnRef.value.getBoundingClientRect()
  }
  settingsOpen.value = !settingsOpen.value
}

const handleSettingsApply = (newOrder, newHidden) => {
  columnOrderState.value = newOrder
  hiddenColumns.value = newHidden
  saveSettings(newOrder, newHidden)
}

const handleSettingsReset = () => {
  columnOrderState.value = [...defaultOrder.value]
  hiddenColumns.value = new Set()
  if (props.storageKey) localStorage.removeItem(`datagrid_col_${props.storageKey}`)
}

/* 페이징 버튼 */
const pageButtons = computed(() => {
  if (props.totalPages <= 0) return []
  const maxVisible = 5
  let start = Math.max(0, props.page - Math.floor(maxVisible / 2))
  let end = Math.min(props.totalPages, start + maxVisible)
  if (end - start < maxVisible) start = Math.max(0, end - maxVisible)
  const items = []
  for (let i = start; i < end; i++) items.push(i)
  return items
})
</script>
