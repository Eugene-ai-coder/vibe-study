<template>
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl w-80 mx-4">
      <div class="px-6 py-5">
        <p class="text-sm text-gray-700">{{ message }}</p>
      </div>
      <div class="flex justify-end gap-2 px-6 pb-5">
        <button
          @click="$emit('cancel')"
          class="h-8 px-4 text-sm text-gray-600 border border-gray-300 rounded-lg hover:border-gray-400 transition-colors"
        >
          {{ cancelText }}
        </button>
        <button
          @click="$emit('confirm')"
          :class="[
            'h-8 px-4 text-sm text-white rounded-lg transition-colors',
            confirmType === 'danger' ? 'bg-red-500 hover:bg-red-600' : 'bg-blue-600 hover:bg-blue-700'
          ]"
        >
          {{ confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, required: true },
  confirmText: { type: String, default: '확인' },
  cancelText: { type: String, default: '취소' },
  confirmType: { type: String, default: 'danger' },
})

defineEmits(['confirm', 'cancel'])
</script>
