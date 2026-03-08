<template>
  <Teleport to="body">
    <div
      ref="popupRef"
      :style="{ position: 'fixed', top: pos.top + 'px', left: pos.left + 'px' }"
      class="w-60 bg-white rounded-lg shadow-lg border border-gray-200 z-[9999]"
      @click.stop
    >
      <!-- 조건 선택 + 텍스트 검색 -->
      <div class="p-3 border-b border-gray-100">
        <label class="block text-xs text-gray-500 mb-1">조건</label>
        <select
          v-model="condition"
          class="w-full h-7 px-2 text-xs border border-gray-300 rounded focus:outline-none focus:border-blue-400 bg-white"
        >
          <option v-for="c in CONDITIONS" :key="c.value" :value="c.value">{{ c.label }}</option>
        </select>
        <input
          v-if="!isNoInputCondition"
          v-model="text"
          @keydown.enter="handleApply"
          placeholder="검색어 입력..."
          class="w-full h-7 px-2 mt-2 text-xs border border-gray-300 rounded focus:outline-none focus:border-blue-400"
          autofocus
        />
      </div>

      <!-- 체크박스 리스트 -->
      <div class="p-3 border-b border-gray-100">
        <label class="block text-xs text-gray-500 mb-2">값 선택</label>
        <label class="flex items-center gap-2 text-xs text-gray-700 mb-1 cursor-pointer hover:text-blue-600">
          <input
            type="checkbox"
            :checked="allChecked"
            @change="toggleAll"
            class="rounded border-gray-300 text-blue-600 focus:ring-blue-500 h-3.5 w-3.5"
          />
          <span class="font-medium">모두 선택</span>
        </label>
        <div class="max-h-32 overflow-y-auto mt-1 space-y-0.5">
          <label
            v-for="val in uniqueValues"
            :key="val"
            class="flex items-center gap-2 text-xs text-gray-600 cursor-pointer py-0.5 px-1 rounded hover:bg-gray-50"
          >
            <input
              type="checkbox"
              :checked="checkedValues.includes(val)"
              @change="toggleValue(val)"
              class="rounded border-gray-300 text-blue-600 focus:ring-blue-500 h-3.5 w-3.5"
            />
            <span class="truncate">{{ val || '(빈 값)' }}</span>
          </label>
        </div>
      </div>

      <!-- 액션 버튼 -->
      <div class="flex items-center justify-between p-2">
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

const CONDITIONS = [
  { value: 'contains',   label: '포함' },
  { value: 'equals',     label: '일치' },
  { value: 'startsWith', label: '시작 문자' },
  { value: 'endsWith',   label: '끝 문자' },
  { value: 'isEmpty',    label: '비어 있음' },
  { value: 'isNotEmpty', label: '비어 있지 않음' },
]

const props = defineProps({
  columnId: { type: String, required: true },
  data: { type: Array, required: true },
  currentFilter: { type: Object, default: null },
  anchorRect: { type: Object, required: true },
})

const emit = defineEmits(['apply', 'close'])

const popupRef = ref(null)
const pos = usePopupPosition(props.anchorRect, popupRef)

const condition = ref(props.currentFilter?.condition ?? 'contains')
const text = ref(props.currentFilter?.text ?? '')
const checkedValues = ref([...(props.currentFilter?.checkedValues ?? [])])

const uniqueValues = computed(() => {
  const vals = new Set()
  props.data.forEach(row => {
    const v = row[props.columnId]
    vals.add(v != null ? String(v) : '')
  })
  return [...vals].sort()
})

const allChecked = computed(() =>
  checkedValues.value.length === uniqueValues.value.length && uniqueValues.value.length > 0
)

const isNoInputCondition = computed(() =>
  condition.value === 'isEmpty' || condition.value === 'isNotEmpty'
)

useClickOutside(popupRef, () => emit('close'))

const toggleAll = () => {
  checkedValues.value = allChecked.value ? [] : [...uniqueValues.value]
}

const toggleValue = (val) => {
  const idx = checkedValues.value.indexOf(val)
  if (idx >= 0) checkedValues.value.splice(idx, 1)
  else checkedValues.value.push(val)
}

const handleApply = () => {
  const hasCondition = condition.value === 'isEmpty' || condition.value === 'isNotEmpty' || text.value.trim() !== ''
  const hasChecked = checkedValues.value.length > 0 && checkedValues.value.length < uniqueValues.value.length

  if (!hasCondition && !hasChecked) {
    emit('apply', null)
  } else {
    emit('apply', {
      condition: hasCondition ? condition.value : undefined,
      text: hasCondition ? text.value : undefined,
      checkedValues: hasChecked ? [...checkedValues.value] : undefined,
    })
  }
  emit('close')
}

const handleReset = () => {
  condition.value = 'contains'
  text.value = ''
  checkedValues.value = []
  emit('apply', null)
  emit('close')
}
</script>
