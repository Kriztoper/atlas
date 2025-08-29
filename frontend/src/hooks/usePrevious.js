import { useRef, useEffect } from 'react'

/**
 * Hook that returns the previous value of a state or prop
 * @param {*} value - The current value
 * @returns {*} previousValue - The previous value
 */
export const usePrevious = (value) => {
  const ref = useRef()
  
  useEffect(() => {
    ref.current = value
  }, [value])
  
  return ref.current
}

export default usePrevious
