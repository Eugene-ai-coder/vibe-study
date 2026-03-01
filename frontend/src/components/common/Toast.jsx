import { useEffect } from 'react'

export default function Toast({ message, type = 'success', onClose }) {
  useEffect(() => {
    if (!message) return
    const timer = setTimeout(onClose, 2000)
    return () => clearTimeout(timer)
  }, [message, onClose])

  if (!message) return null

  const colorClass = type === 'success'
    ? 'bg-[#2563EB] text-white'
    : 'bg-red-500 text-white'

  return (
    <div className={`fixed top-5 right-5 z-50 px-5 py-3 rounded-lg shadow-lg text-sm font-medium
                     transition-opacity ${colorClass}`}>
      {message}
    </div>
  )
}
