import { useState, useCallback } from 'react';

/**
 * Custom hook for managing async operations with loading and error states
 */
export const useAsyncOperation = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = useCallback(async (asyncFunction) => {
    setLoading(true);
    setError(null);
    
    try {
      const result = await asyncFunction();
      return result;
    } catch (err) {
      const errorMessage = err.message || '操作に失敗しました';
      setError(errorMessage);
      console.error('Async operation error:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setLoading(false);
    setError(null);
  }, []);

  return {
    loading,
    error,
    execute,
    reset
  };
};

/**
 * Custom hook for managing form validation
 */
export const useFormValidation = (validationRules) => {
  const [errors, setErrors] = useState({});

  const validate = useCallback((values) => {
    const newErrors = {};
    
    Object.keys(validationRules).forEach(field => {
      const rules = validationRules[field];
      const value = values[field];
      
      for (const rule of rules) {
        if (rule.test && !rule.test(value)) {
          newErrors[field] = rule.message;
          break;
        }
      }
    });
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [validationRules]);

  const clearErrors = useCallback(() => {
    setErrors({});
  }, []);

  return {
    errors,
    validate,
    clearErrors
  };
};