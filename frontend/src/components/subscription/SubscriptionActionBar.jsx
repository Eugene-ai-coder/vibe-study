export default function SubscriptionActionBar({ onRegister, onUpdate, onDelete }) {
  return (
    <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-3">
      <div className="max-w-6xl mx-auto flex justify-end gap-3">
        <button onClick={onRegister}
          className="h-10 px-6 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">
          등록
        </button>
        <button onClick={onUpdate}
          className="h-10 px-6 bg-white border border-gray-300 text-gray-700 rounded-lg text-sm hover:bg-gray-50">
          변경
        </button>
        <button onClick={onDelete}
          className="h-10 px-6 bg-white border border-red-300 text-red-600 rounded-lg text-sm hover:bg-red-50">
          삭제
        </button>
      </div>
    </div>
  )
}
