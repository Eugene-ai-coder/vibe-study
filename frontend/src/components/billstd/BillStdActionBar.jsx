export default function BillStdActionBar({ onSave, onChange, onDelete }) {
  return (
    <div className="fixed bottom-0 left-0 right-0 z-10 bg-white border-t border-gray-200 shadow-lg">
      <div className="max-w-6xl mx-auto px-6 py-3 flex justify-end gap-3">
        <button
          onClick={onSave}
          className="h-8 px-5 bg-[#2563EB] hover:bg-blue-700 text-white text-sm font-medium
                     rounded-md transition-colors"
        >
          저장
        </button>
        <button
          onClick={onChange}
          className="h-8 px-5 bg-[#2563EB] hover:bg-blue-700 text-white text-sm font-medium
                     rounded-md transition-colors"
        >
          변경
        </button>
        <button
          onClick={onDelete}
          className="h-8 px-5 bg-red-500 hover:bg-red-600 text-white text-sm font-medium
                     rounded-md transition-colors"
        >
          삭제
        </button>
      </div>
    </div>
  )
}
