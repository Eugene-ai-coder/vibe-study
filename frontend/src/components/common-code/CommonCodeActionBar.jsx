import FloatingActionBar from '../common/FloatingActionBar'

export default function CommonCodeActionBar({ onSave, onCancel, activeMode }) {
  if (activeMode === 'view') return null

  return (
    <FloatingActionBar>
      <button
        onClick={onCancel}
        className="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors"
      >
        취소
      </button>
      <button
        onClick={onSave}
        className="h-8 px-6 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors"
      >
        저장
      </button>
    </FloatingActionBar>
  )
}
