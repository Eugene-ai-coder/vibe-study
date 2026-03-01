import Header from './Header'

export default function Layout({ children, maxWidth = 'max-w-3xl' }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <div className={`${maxWidth} mx-auto px-4 py-10`}>
        {children}
      </div>
    </div>
  )
}
