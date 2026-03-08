<template>
  <Teleport to="body">
    <div
      ref="popupRef"
      :style="{ position: 'fixed', top: pos.top + 'px', left: pos.left + 'px' }"
      class="w-60 bg-white rounded-lg shadow-lg border border-gray-200 z-[9999]"
      @click.stop
    >
      <!-- 헤더 -->
      <div class="px-3 py-2 border-b border-gray-100">
        <div class="flex items-center justify-between">
          <span class="text-xs font-semibold text-gray-700">컬럼 설정</span>
          <span class="text-[10px] text-gray-400">{{ visibleCount }}/{{ localOrder.length }}개 표시</span>
        </div>
      </div>

      <!-- 컬럼 리스트 (드래그 가능) -->
      <div class="max-h-64 overflow-y-auto p-2">
        <div
          v-for="(colId, index) in localOrder"
          :key="colId"
          draggable="true"
          @dragstart="onDragStart($event, index)"
          @dragover.prevent
          @drop="onDrop($event, index)"
          :class="[
            'flex items-center gap-2 px-2 py-1.5 rounded text-xs select-none',
            dragIndex === index ? 'bg-blue-50 shadow-md ring-1 ring-blue-300' : 'hover:bg-gray-50'
          ]"
        >
          <!-- 드래그 핸들 -->
          <span class="cursor-grab active:cursor-grabbing p-0.5 rounded hover:bg-gray-200 text-gray-400">⠿</span>

          <!-- 체크박스 -->
          <label class="flex items-center gap-2 flex-1 min-w-0 cursor-pointer">
            <input
              type="checkbox"
              :checked="!localHidden.has(colId)"
              @change="toggleVisibility(colId)"
              :disabled="!localHidden.has(colId) && visibleCount <= 1"
              class="rounded border-gray-300 text-blue-600 focus:ring-blue-500 h-3.5 w-3.5 disabled:opacity-40"
            />
            <span :class="['truncate', localHidden.has(colId) ? 'text-gray-400' : 'text-gray-700']">
              {{ labelMap[colId] ?? colId }}
            </span>
          </label>
        </div>
      </div>

      <!-- 액션 버튼 -->
      <div class="flex items-center justify-between p-2 border-t border-gray-100">
        <button
          @click="handleReset"
          class="h-7 px-3 text-xs text-gray-500 hover:text-red-500 hover:bg-red-50 rounded"
        >
          초기화
        </button>
        <button
          @click="handleApply"
          class="h-7 px-4 text-xs bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          적용
        </button>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue'
import useClickOutside from '../../composables/useClickOutside'
import usePopupPosition from '../../composables/usePopupPosition'

const props = defineProps({
  columns: { type: Array, required: true },
  columnOrder: { type: Array, required: true },
  hiddenColumns: { type: Set, required: true },
  anchorRect: { type: Object, required: true },
})

const emit = defineEmits(['apply', 'reset', 'close'])

const popupRef = ref(null)
const pos = usePopupPosition(props.anchorRect, popupRef, { alignRight: true })

const localOrder = ref([...props.columnOrder])
const localHidden = ref(new Set(props.hiddenColumns))
const dragIndex = ref(null)

const labelMap = computed(() => {
  const map = {}
  props.columns.forEach(col => {
    const key = col.accessorKey ?? col.id
    map[key] = typeof col.header === 'string' ? col.header : key
  })
  return map
})

const visibleCount = computed(() =>
  localOrder.value.filter(id => !localHidden.value.has(id)).length
)

useClickOutside(popupRef, () => emit('close'))

const onDragStart = (e, index) => {
  dragIndex.value = index
  e.dataTransfer.effectAllowed = 'move'
}

const onDrop = (e, targetIndex) => {
  e.preventDefault()
  if (dragIndex.value === null || dragIndex.value === targetIndex) return
  const arr = [...localOrder.value]
  const [removed] = arr.splice(dragIndex.value, 1)
  arr.splice(targetIndex, 0, removed)
  localOrder.value = arr
  dragIndex.value = null
}

const toggleVisibility = (colId) => {
  const next = new Set(localHidden.value)
  if (next.has(colId)) {
    next.delete(colId)
  } else {
    if (visibleCount.value <= 1) return
    next.add(colId)
  }
  localHidden.value = next
}

const handleApply = () => {
  emit('apply', localOrder.value, localHidden.value)
  emit('close')
}

const handleReset = () => {
  emit('reset')
  emit('close')
}
</script>
