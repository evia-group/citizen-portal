"use client";

import { useDogApplication } from "@repo/applications";
import { use } from "react";
import { Input } from "~/components/ui/input";
import { Text } from "~/components/ui/text";
import { H2 } from "~/components/ui/typography";

export default function ApplicationDogDetail(props: {
  params: Promise<{ applicationId: string }>;
}) {
  const params = use(props.params);
  const { data, isLoading, isError, error } = useDogApplication(
    Number(params.applicationId),
  );

  const labelClassName =
    "block mb-2 text-sm font-medium text-gray-900 dark:text-white";

  return (
    <div className="container mx-auto mt-5">
      <div className="mt-5">
        <div className="w-full">
          <H2 className="mb-3">Angaben zum Hund </H2>
        </div>

        {isLoading && <Text>Loading...</Text>}
        {isError && <Text>Error... {error.message}</Text>}

        {data && (
          <div className="flex flex-col gap-3 mb-3">
            <div>
              <label className={labelClassName} htmlFor="dog-tax-stamp-number">
                Nummer der Hundesteuermarke (freiwillige Angabe)
              </label>
              <Input
                id="dog-tax-stamp-number"
                value={data.dog?.taxStampNumber ?? "-"}
                readOnly
              />
            </div>
            <div>
              <label className={labelClassName} htmlFor="dog-name">
                Name vom Hund
              </label>
              <Input id="dog-name" value={data.dog?.name ?? "-"} readOnly />
            </div>
            <div>
              <label className={labelClassName} htmlFor="dog-race">
                Hunderasse
              </label>
              <Input id="dog-race" value={data.dog?.race ?? "-"} readOnly />
            </div>
            <div>
              <span className={labelClassName}>Grund für den Ersatz :</span>
              {data?.justification === "STAMP_UNUSABLE" && (
                <div className="flex items-center">
                  <span>Marke unbrauchbar</span>
                </div>
              )}
              {data?.justification === "LOST_STAMP" && (
                <div className="flex items-center">
                  <span>Marke verloren</span>
                </div>
              )}
              {!data?.justification && (
                <div className="flex items-center">
                  <span>Keine Angabe</span>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
