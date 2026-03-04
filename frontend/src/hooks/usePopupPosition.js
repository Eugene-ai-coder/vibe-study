import { useState, useEffect } from 'react'

export default function usePopupPosition(anchorRect, popupRef, { alignRight = false } = {}) {
  const [pos, setPos] = useState({ top: 0, left: 0 })

  useEffect(() => {
    if (!anchorRect) return
    const popupEl = popupRef.current
    const top = anchorRect.bottom + 4
    let left = alignRight ? anchorRect.right - 240 : anchorRect.left

    if (popupEl) {
      const rect = popupEl.getBoundingClientRect()
      if (left + rect.width > window.innerWidth - 8) {
        left = window.innerWidth - rect.width - 8
      }
      if (left < 8) left = 8
      if (top + rect.height > window.innerHeight - 8) {
        setPos({ top: anchorRect.top - rect.height - 4, left: Math.max(8, left) })
        return
      }
    }
    setPos({ top, left: Math.max(8, left) })
  }, [anchorRect, popupRef, alignRight])

  return pos
}
