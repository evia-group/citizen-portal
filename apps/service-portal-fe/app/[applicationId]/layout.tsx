/** @jsxImportSource react */
"use client";

import { Button, Text } from "@repo/ui";
import Link from "next/link";
import { use } from "react";

export default function ApplicationLayout(props: {
  children: React.ReactNode;
  params: Promise<{ applicationId: string }>;
}) {
  const params = use(props.params);

  const { applicationId } = params;

  const { children } = props;

  return (
    <div className="container mx-auto mt-5">
      <div className=" flex row">
        <div className="col-md-2 mx-2">
          <Button>
            <Link href={`/${applicationId}`}>
              <Text>Persönliche Angaben</Text>
            </Link>
          </Button>
        </div>
        <div className="col-md-2 mx-2">
          <Button>
            <Link href={`/${applicationId}/dog-detail`}>
              <Text>Angaben zum Hund</Text>
            </Link>
          </Button>
        </div>
        <div className="col-md-2 mx-2">
          <Button>
            <Link href={`/${applicationId}/document`}>
              <Text>Dokumente</Text>
            </Link>
          </Button>
        </div>
        <div className="col-md-2 mx-2">
          <Button>
            <Link href={`/${applicationId}/cost`}>
              <Text>Kosten</Text>
            </Link>
          </Button>
        </div>
        <div className="col-md-2 mx-2">
          <Button>
            <Link href={`/${applicationId}/mailbox`}>
              <Text>Nachrichten</Text>
            </Link>
          </Button>
        </div>
      </div>

      <div className="py-7">{children}</div>
    </div>
  );
}
