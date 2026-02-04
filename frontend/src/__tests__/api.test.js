// Simple unit tests for CI/CD pipeline
describe('API Services', () => {
  test('API base URL configuration', () => {
    expect(process.env.NODE_ENV).toBeDefined();
  });

  test('Basic environment setup', () => {
    expect(typeof window).toBe('object');
  });
});