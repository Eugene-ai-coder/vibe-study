import { ref } from 'vue'

const message = ref('')
const type = ref('success')
let timer = null

export function useToast() {
  const showSuccess = (msg) => {
    clearTimeout(timer)
    message.value = msg
    type.value = 'success'
    timer = setTimeout(() => { message.value = '' }, 3000)
  }

  const showError = (msg) => {
    clearTimeout(timer)
    message.value = msg
    type.value = 'error'
    timer = setTimeout(() => { message.value = '' }, 3000)
  }

  const close = () => {
    clearTimeout(timer)
    message.value = ''
  }

  return { message, type, showSuccess, showError, close }
}
