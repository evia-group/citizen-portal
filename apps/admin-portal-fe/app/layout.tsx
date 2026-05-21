/** @jsxImportSource react */

import "@repo/tailwind-config/global.css";
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Admin Portal",
  description: "Portal for admin users",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
