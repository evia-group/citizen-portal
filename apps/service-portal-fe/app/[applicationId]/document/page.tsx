"use client";

import { useApplication } from "@repo/applications";
import { Text } from "~/components/ui/text";
import { H2 } from "~/components/ui/typography";

export default function ApplicationDocument({
  params,
}: { params: { applicationId: number } }) {
  const { data, isLoading, isError, error } = useApplication(
    params.applicationId,
  );

  return (
    <div className="container mx-auto mt-5">
      <div className="mt-5">
        <div className="w-full">
          <H2 className="mb-3">Dokumente:</H2>
        </div>

        {isLoading && <Text>Loading...</Text>}
        {isError && <Text>Error... {error.message}</Text>}

        {data && (
          <div className="flex flex-col gap-3 my-3">
            <div>
              <span> Es gibt keine Dokumente zu diesem Antrag. </span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
