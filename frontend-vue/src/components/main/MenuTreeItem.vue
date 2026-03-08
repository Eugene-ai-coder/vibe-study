<template>
  <!-- 리프 메뉴 (URL 있고 자식 없음) -->
  <template v-if="!node.children || node.children.length === 0">
    <router-link
      v-if="node.menuUrl"
      :to="node.menuUrl"
      :class="linkClass(node.menuUrl)"
      :style="{ paddingLeft: depth * 16 + 'px' }"
    >
      {{ node.menuNm }}
    </router-link>
  </template>

  <!-- 그룹 메뉴 (자식 있음) -->
  <template v-else>
    <button
      type="button"
      @click="$emit('toggle', node.menuId)"
      class="w-full flex items-center justify-between py-2 pr-4 text-sm text-gray-500 hover:bg-gray-50 transition-colors"
      :style="{ paddingLeft: depth * 16 + 'px' }"
    >
      <span>{{ node.menuNm }}</span>
      <span class="text-xs text-gray-400">{{ openGroups.has(node.menuId) ? '▾' : '▸' }}</span>
    </button>
    <template v-if="openGroups.has(node.menuId)">
      <menu-tree-item
        v-for="child in node.children"
        :key="child.menuId"
        :node="child"
        :depth="depth + 1"
        :open-groups="openGroups"
        :current-path="currentPath"
        @toggle="$emit('toggle', $event)"
      />
    </template>
  </template>
</template>

<script setup>
defineProps({
  node: { type: Object, required: true },
  depth: { type: Number, default: 1 },
  openGroups: { type: Set, required: true },
  currentPath: { type: String, default: '' },
})

defineEmits(['toggle'])

const linkClass = (to) => {
  const isActive = window.location.pathname === to
  return `block py-2 pr-4 text-sm transition-colors ${
    isActive
      ? 'text-[#2563EB] bg-blue-50 font-medium'
      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-800'
  }`
}
</script>
