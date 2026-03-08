import { ref, onMounted, nextTick } from 'vue'

export default function usePopupPosition(anchorRect, popupRef, { alignRight = false } = {}) {
  const pos = ref({ top: 0, left: 0 })

  onMounted(async () => {
    if (!anchorRect) return
    await nextTick()
    const popupEl = popupRef.value
    const top = anchorRect.bottom + 4
    let left = alignRight ? anchorRect.right - 240 : anchorRect.left

    if (popupEl) {
      const rect = popupEl.getBoundingClientRect()
      if (left + rect.width > window.innerWidth - 8) {
        left = window.innerWidth - rect.width - 8
      }
      if (left < 8) left = 8
      if (top + rect.height > window.innerHeight - 8) {
        pos.value = { top: anchorRect.top - rect.height - 4, left: Math.max(8, left) }
        return
      }
    }
    pos.value = { top, left: Math.max(8, left) }
  })

  return pos
}
