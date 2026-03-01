export default function SubscriptionMainActionBar({ onSave }) {
  return (
    <div className="fixed bottom-0 left-56 right-0 bg-white border-t border-gray-200 px-6 py-3 flex justify-end gap-2">
      <button
        onClick={onSave}
        className="h-8 px-5 bg-[#2563EB] text-white text-sm rounded hover:bg-blue-700"
      >
        저장
      </button>
    </div>
  )
}
