<template>
  <div v-if="$slots.default" class="sticky bottom-0 z-10 bg-white border-t border-gray-200 shadow-lg -mx-6 -mb-6">
    <div class="max-w-6xl mx-auto px-6 py-3 flex justify-end gap-3">
      <slot />
    </div>
  </div>
</template>
