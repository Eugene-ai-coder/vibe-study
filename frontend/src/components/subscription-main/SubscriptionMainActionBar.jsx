import FloatingActionBar from '../common/FloatingActionBar'

export default function SubscriptionMainActionBar({ onSave }) {
  return (
    <FloatingActionBar>
      <button
        onClick={onSave}
        className="h-8 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm transition-colors"
      >
        저장
      </button>
    </FloatingActionBar>
  )
}
