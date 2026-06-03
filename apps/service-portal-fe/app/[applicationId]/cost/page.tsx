"use client";

import { useApplication } from "@repo/applications";
import { statusMap } from "@repo/services/models/dog-application";
import { use } from "react";
import { Text } from "~/components/ui/text";
import { H2 } from "~/components/ui/typography";

export default function ApplicationCost(props: {
  params: Promise<{ applicationId: number }>;
}) {
  const params = use(props.params);
  const { data, isLoading, isError, error } = useApplication(
    params.applicationId,
  );

  const labelClassName =
    "block mb-2 text-sm font-medium text-gray-900 dark:text-white";

  return (
    <div className="container mx-auto mt-5">
      <div className="mt-5">
        <div className="w-full">
          <H2 className="mb-3">
            Rechnung zum Service &quot;{data?.service.name ?? "-"}&quot;
          </H2>
        </div>

        {isLoading && <Text>Loading...</Text>}
        {isError && <Text>Error... {error.message}</Text>}

        {data && (
          <div className="flex flex-col gap-3 mb-3">
            <div className="mt-3">
              <span className={labelClassName}>
                Rechnungsbetrag: {data.service.cost ?? "-"} Euro
              </span>
            </div>

            <div>
              <span className={labelClassName}>
                Fälligkeitsdatum: {data.createdDate ?? "-"}
              </span>
            </div>

            <div>
              <span className={labelClassName}>
                Status: {statusMap[data.status]}
              </span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
