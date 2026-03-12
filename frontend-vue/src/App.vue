<template>
  <router-view v-slot="{ Component, route }">
    <template v-if="route.meta.public">
      <component :is="Component" />
    </template>
    <MainLayout v-else>
      <keep-alive :include="tabStore.cachedNames">
        <component :is="Component" :key="route.path" />
      </keep-alive>
    </MainLayout>
  </router-view>
</template>

<script setup>
import MainLayout from './components/common/MainLayout.vue'
import { useTabStore } from './stores/tab'

const tabStore = useTabStore()
</script>
