<template>
  <div :data-menu-id="node.menuId">
    <div
      :class="[
        'flex items-center group cursor-grab active:cursor-grabbing transition-colors',
        node.menuId === selectedId
          ? 'bg-blue-50 text-blue-900'
          : 'hover:bg-gray-50 text-gray-800'
      ]"
      style="height: 32px;"
      @click="$emit('select', node)"
    >
      <!-- 인덴트 가이드 라인 -->
      <div class="flex items-center flex-shrink-0" style="height: 32px;">
        <!-- 상위 depth별 세로선 -->
        <span
          v-for="(hasLine, i) in ancestors"
          :key="i"
          class="relative flex-shrink-0"
          style="width: 20px; height: 32px;"
        >
          <span
            v-if="hasLine"
            class="absolute left-[9px] top-0 w-px h-full bg-gray-300"
          ></span>
        </span>

        <!-- 현재 노드 커넥터 (├── 또는 └──) -->
        <span
          v-if="depth > 0"
          class="relative flex-shrink-0"
          style="width: 20px; height: 32px;"
        >
          <!-- 세로선: 마지막이면 절반만, 아니면 전체 -->
          <span
            :class="[
              'absolute left-[9px] w-px bg-gray-300',
              isLast ? 'top-0 h-1/2' : 'top-0 h-full'
            ]"
          ></span>
          <!-- 가로선 -->
          <span
            class="absolute left-[9px] top-1/2 h-px bg-gray-300"
            style="width: 10px;"
          ></span>
        </span>
      </div>

      <!-- 토글 버튼 -->
      <span
        v-if="hasChildren"
        class="flex-shrink-0 w-4 h-4 flex items-center justify-center text-gray-500 hover:text-blue-600 mr-0.5"
        @click.stop="expanded = !expanded"
      >
        <svg
          :class="['w-3 h-3 transition-transform duration-150', expanded ? 'rotate-90' : '']"
          viewBox="0 0 12 12" fill="currentColor"
        >
          <path d="M4.5 2L9 6L4.5 10V2Z"/>
        </svg>
      </span>
      <span v-else class="w-4 flex-shrink-0 mr-0.5"></span>

      <!-- 폴더/문서 아이콘 -->
      <svg v-if="hasChildren" class="w-4 h-4 flex-shrink-0 mr-1.5" :class="expanded ? 'text-yellow-500' : 'text-yellow-600'" viewBox="0 0 20 20" fill="currentColor">
        <path v-if="expanded" d="M2 6a2 2 0 012-2h5l2 2h5a2 2 0 012 2v1H4a2 2 0 00-2 2v3l1.5-6H18l-1.8 7.2A2 2 0 0114.26 16H4a2 2 0 01-2-2V6z"/>
        <path v-else d="M2 6a2 2 0 012-2h5l2 2h5a2 2 0 012 2v6a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"/>
      </svg>
      <svg v-else class="w-4 h-4 flex-shrink-0 mr-1.5 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd"/>
      </svg>

      <!-- 메뉴명 -->
      <span class="flex-1 text-sm truncate leading-none">
        {{ node.menuNm }}
        <span v-if="node.menuUrl" class="text-xs text-gray-400 ml-1">{{ node.menuUrl }}</span>
        <span v-if="node.useYn === 'N'" class="text-xs text-red-400 ml-1">[미사용]</span>
      </span>

      <!-- 추가 버튼 (URL이 없는 그룹메뉴만) -->
      <div v-if="!node.menuUrl" class="hidden group-hover:inline-flex items-center gap-0.5 mr-1">
        <button
          @click.stop="$emit('add-child', node)"
          class="w-5 h-5 text-xs text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded flex items-center justify-center"
          title="하위 메뉴 추가"
        >+</button>
      </div>
    </div>

    <!-- 자식 노드 재귀 (항상 렌더링하여 드롭 영역 확보) -->
    <div class="sortable-group"
         :data-parent-id="node.menuId"
         :data-has-url="node.menuUrl ? 'true' : undefined">
      <template v-if="expanded">
        <tree-node
          v-for="(child, idx) in node.children"
          :key="child.menuId"
          :node="child"
          :depth="depth + 1"
          :selected-id="selectedId"
          :is-first="idx === 0"
          :is-last="idx === node.children.length - 1"
          :ancestors="childAncestors"
          @select="$emit('select', $event)"
          @add-child="$emit('add-child', $event)"
        />
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  node: { type: Object, required: true },
  depth: { type: Number, default: 0 },
  selectedId: { type: String, default: null },
  isFirst: { type: Boolean, default: false },
  isLast: { type: Boolean, default: true },
  ancestors: { type: Array, default: () => [] },
})

defineEmits(['select', 'add-child'])

const expanded = ref(true)

const hasChildren = computed(() => props.node.children && props.node.children.length > 0)

// 자식에게 전달할 ancestors 배열: 현재 노드가 마지막이 아니면 세로선을 이어야 함
const childAncestors = computed(() => [...props.ancestors, !props.isLast])
</script>
