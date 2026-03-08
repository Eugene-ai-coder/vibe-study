<template>
  <aside class="w-56 bg-white border-r border-gray-200 flex-shrink-0 overflow-y-auto">
    <nav class="py-4">
      <template v-for="node in menuTree" :key="node.menuId">
        <!-- 최상위: URL 있으면 링크, 없으면 그룹 -->
        <div v-if="!node.children || node.children.length === 0" class="mb-2">
          <router-link v-if="node.menuUrl" :to="node.menuUrl" :class="linkClass(node.menuUrl)">
            {{ node.menuNm }}
          </router-link>
        </div>
        <div v-else class="mb-1">
          <button
            type="button"
            @click="toggleGroup(node.menuId)"
            class="w-full flex items-center justify-between px-4 py-2 text-sm font-semibold text-gray-500 uppercase tracking-wider hover:bg-gray-50 transition-colors"
          >
            <span>{{ node.menuNm }}</span>
            <span class="text-xs text-gray-400">{{ openGroups.has(node.menuId) ? '▾' : '▸' }}</span>
          </button>
          <template v-if="openGroups.has(node.menuId)">
            <menu-tree-item
              v-for="child in node.children"
              :key="child.menuId"
              :node="child"
              :depth="2"
              :open-groups="openGroups"
              :current-path="route.path"
              @toggle="toggleGroup"
            />
          </template>
        </div>
      </template>
    </nav>
  </aside>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useMenuStore } from '../../stores/menu'
import MenuTreeItem from './MenuTreeItem.vue'

const route = useRoute()
const menuStore = useMenuStore()

const menuTree = computed(() => menuStore.menuTree)

const openGroups = ref(new Set())

function findGroupForPath(pathname, nodes) {
  for (const node of nodes) {
    if (node.menuUrl === pathname) return node.menuId
    if (node.children && node.children.length > 0) {
      for (const child of node.children) {
        if (child.menuUrl === pathname) return node.menuId
        if (child.children && child.children.length > 0) {
          const found = findGroupForPath(pathname, [child])
          if (found) {
            openGroups.value = new Set([...openGroups.value, node.menuId])
            return found
          }
        }
      }
    }
  }
  return null
}

const initGroup = () => {
  const activeGroup = findGroupForPath(route.path, menuTree.value)
  if (activeGroup && !openGroups.value.has(activeGroup)) {
    openGroups.value = new Set([...openGroups.value, activeGroup])
  }
}

watch(() => route.path, initGroup)
watch(menuTree, initGroup)

onMounted(() => {
  if (menuTree.value.length > 0) {
    initGroup()
  }
})

const toggleGroup = (groupId) => {
  const next = new Set(openGroups.value)
  if (next.has(groupId)) next.delete(groupId)
  else next.add(groupId)
  openGroups.value = next
}

const linkClass = (to) => {
  const isActive = route.path === to
  return `block px-4 py-2 text-sm transition-colors ${
    isActive
      ? 'text-[#2563EB] bg-blue-50 font-medium'
      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-800'
  }`
}
</script>
