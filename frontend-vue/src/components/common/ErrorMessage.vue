<template>
  <div class="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center gap-4">
    <p class="text-red-700 text-sm">⚠️ {{ message }}</p>
    <button
      v-if="onRetry"
      @click="onRetry"
      class="bg-red-600 hover:bg-red-700 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
    >
      다시 시도
    </button>
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, required: true },
  onRetry: { type: Function, default: null },
})
</script>
