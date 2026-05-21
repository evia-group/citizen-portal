/** @jsxImportSource react */
"use client";

import { Button, Text } from "@repo/ui";
import Link from "next/link";
import { forwardRef } from "react";

// `onClick`, `href`, and `ref` need to be passed to the DOM element
// for proper handling
const ButtonLink = forwardRef<
  HTMLAnchorElement,
  { onClick?: () => void; href?: string; children: React.ReactNode }
>(({ onClick, href, children }, ref) => {
  return (
    <Button>
      <a href={href} onClick={onClick} ref={ref}>
        <Text>{children}</Text>
      </a>
    </Button>
  );
});
ButtonLink.displayName = "ButtonLink";

export default function ApplicationLayout({
  children,
  params: { applicationId },
}: { children: React.ReactNode; params: { applicationId: string } }) {
  return (
    <div className="container mx-auto mt-5">
      <div className=" flex row">
        <div className="col-md-2 mx-2">
          <Link href={`/${applicationId}`} passHref legacyBehavior>
            <ButtonLink>
              <Text>Persönliche Angaben</Text>
            </ButtonLink>
          </Link>
        </div>
        <div className="col-md-2 mx-2">
          <Link href={`/${applicationId}/dog-detail`} passHref legacyBehavior>
            <ButtonLink>
              <Text>Angaben zum Hund</Text>
            </ButtonLink>
          </Link>
        </div>
        <div className="col-md-2 mx-2">
          <Link href={`/${applicationId}/document`} passHref legacyBehavior>
            <ButtonLink>
              <Text>Dokumente</Text>
            </ButtonLink>
          </Link>
        </div>
        <div className="col-md-2 mx-2">
          <Link href={`/${applicationId}/cost`} passHref legacyBehavior>
            <ButtonLink>
              <Text>Kosten</Text>
            </ButtonLink>
          </Link>
        </div>
        <div className="col-md-2 mx-2">
          <Link href={`/${applicationId}/mailbox`} passHref legacyBehavior>
            <ButtonLink>
              <Text>Nachrichten</Text>
            </ButtonLink>
          </Link>
        </div>
      </div>

      <div className="py-7">{children}</div>
    </div>
  );
}
