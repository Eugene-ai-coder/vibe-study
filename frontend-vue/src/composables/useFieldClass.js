export const fieldClass = (readOnly) => [
  'w-full h-8 border rounded px-2 text-sm',
  readOnly ? 'bg-gray-50 text-gray-400 border-gray-200' : 'bg-white border-gray-300 focus:outline-none focus:border-blue-400',
]
