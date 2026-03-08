import { onMounted, onUnmounted } from 'vue'

export default function useClickOutside(refEl, onClose) {
  const handler = (e) => {
    const el = refEl.value
    if (el && !el.contains(e.target)) onClose()
  }

  onMounted(() => document.addEventListener('mousedown', handler))
  onUnmounted(() => document.removeEventListener('mousedown', handler))
}
