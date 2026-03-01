export default function ErrorMessage({ message, onRetry }) {
  return (
    <div className="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center gap-4">
      <p className="text-red-700 text-sm">⚠️ {message}</p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="bg-red-600 hover:bg-red-700 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
        >
          다시 시도
        </button>
      )}
    </div>
  )
}
