export function formatDate(date: string) {
  return new Date(date).toLocaleDateString("de-DE", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}
