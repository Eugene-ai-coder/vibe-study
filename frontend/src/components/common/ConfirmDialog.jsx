export default function ConfirmDialog({ message, onConfirm, onCancel }) {
  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-80 mx-4">
        <div className="px-6 py-5">
          <p className="text-sm text-gray-700">{message}</p>
        </div>
        <div className="flex justify-end gap-2 px-6 pb-5">
          <button
            onClick={onCancel}
            className="h-8 px-4 text-sm text-gray-600 border border-gray-300 rounded-lg hover:border-gray-400 transition-colors"
          >
            취소
          </button>
          <button
            onClick={onConfirm}
            className="h-8 px-4 text-sm text-white bg-red-500 hover:bg-red-600 rounded-lg transition-colors"
          >
            삭제
          </button>
        </div>
      </div>
    </div>
  )
}
