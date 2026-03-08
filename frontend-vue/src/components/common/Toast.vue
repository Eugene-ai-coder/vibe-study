<template>
  <div
    v-if="message"
    :class="[
      'fixed top-5 left-1/2 -translate-x-1/2 z-50 px-5 py-3 rounded-lg shadow-lg text-sm font-medium whitespace-nowrap transition-opacity',
      type === 'success' ? 'bg-blue-600 text-white' : 'bg-red-500 text-white'
    ]"
  >
    {{ message }}
  </div>
</template>

<script setup>
import { watch } from 'vue'

const props = defineProps({
  message: { type: String, default: '' },
  type: { type: String, default: 'success' },
})

const emit = defineEmits(['close'])

watch(() => props.message, (val) => {
  if (!val) return
  const timer = setTimeout(() => emit('close'), 3000)
  return () => clearTimeout(timer)
})
</script>
