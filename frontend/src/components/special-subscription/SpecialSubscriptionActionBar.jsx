import FloatingActionBar from '../common/FloatingActionBar'

export default function SpecialSubscriptionActionBar({ onNew, onSave, onDelete }) {
  return (
    <FloatingActionBar>
      <button
        onClick={onDelete}
        className="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors"
      >
        삭제
      </button>
      <button
        onClick={onNew}
        className="h-8 px-4 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded text-sm transition-colors"
      >
        신규
      </button>
      <button
        onClick={onSave}
        className="h-8 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm transition-colors"
      >
        저장
      </button>
    </FloatingActionBar>
  )
}
