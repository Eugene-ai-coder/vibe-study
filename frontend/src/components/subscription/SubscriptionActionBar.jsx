import FloatingActionBar from '../common/FloatingActionBar'

export default function SubscriptionActionBar({ onRegister, onUpdate, onDelete }) {
  return (
    <FloatingActionBar>
      <button onClick={onDelete}
        className="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors">
        삭제
      </button>
      <button onClick={onUpdate}
        className="h-8 px-4 border border-blue-600 text-blue-600 hover:bg-blue-50 rounded text-sm transition-colors">
        변경
      </button>
      <button onClick={onRegister}
        className="h-8 px-6 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm transition-colors">
        등록
      </button>
    </FloatingActionBar>
  )
}
