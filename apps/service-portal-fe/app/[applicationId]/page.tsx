"use client";

import { useApplication, useUpdateApplication } from "@repo/applications";
import { useRouter } from "next/navigation";
import { use } from "react";
import { Button } from "~/components/ui/button";
import { Input } from "~/components/ui/input";
import { Text } from "~/components/ui/text";
import { H2 } from "~/components/ui/typography";

export default function ApplicationDetail(props: {
  params: Promise<{ applicationId: string }>;
}) {
  const params = use(props.params);
  const { data, isLoading, isError, error } = useApplication(
    Number(params.applicationId),
  );
  const router = useRouter();
  const update = useUpdateApplication();

  const inputClassName =
    "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500";
  const labelClassName =
    "block mb-2 text-sm font-medium text-gray-900 dark:text-white";

  return (
    <div className="mt-5">
      {isLoading && <Text>Loading...</Text>}
      {isError && <Text>Error... {error.message}</Text>}
      {data && (
        <div className="w-full">
          <H2 className="mb-3">Persönliche Angaben </H2>
          <div className="flex flex-row gap-3 mb-3">
            <div>
              <label htmlFor="Vorname" className={labelClassName}>
                Vorname
              </label>
              <Input value={data.profile?.firstName ?? "-"} readOnly />
            </div>
            <div>
              <label htmlFor="Nachname" className={labelClassName}>
                Nachname
              </label>
              <Input value={data.profile?.lastName ?? "-"} readOnly />
            </div>
          </div>

          <H2 className="mb-3">Wohnanschrift</H2>

          <div className="flex flex-row gap-3 mb-3 items-end">
            <div>
              <label htmlFor="Postleitzahl" className={labelClassName}>
                Postleitzahl
              </label>
              <Input
                value={data.profile?.address?.zipCode.toString() ?? "-"}
                readOnly
              />
            </div>
            <div>
              <label htmlFor="Ort" className={labelClassName}>
                Ort
              </label>
              <Input value={data.profile?.address?.city ?? "-"} readOnly />
            </div>
            <div>
              <label htmlFor="Straße" className={labelClassName}>
                Straße{" "}
              </label>
              <Input value={data.profile?.address?.street ?? "-"} readOnly />
            </div>
            <div>
              <label htmlFor="Hausnummer" className={labelClassName}>
                Hausnummer
              </label>
              <Input
                value={data.profile?.address?.houseNumber.toString() ?? "-"}
                readOnly
              />
            </div>
            <div>
              <label htmlFor="Hausnummerzusatz" className={labelClassName}>
                Hausnummerzusatz
              </label>
              <input
                type="text"
                id="Hausnummerzusatz"
                className={inputClassName}
                placeholder="Hausnummerzusatz"
                required
              />
            </div>
          </div>

          <H2 className="mb-3">Kontaktdaten</H2>

          <div className="flex flex-row gap-3 mb-3 items-end">
            <div>
              <label htmlFor="Telefonnummer" className={labelClassName}>
                Telefonnummer (freiwillige Angabe)
              </label>
              <Input
                value={data.profile?.contactData?.phone?.toString() ?? "-"}
                readOnly
              />
            </div>
          </div>
          <H2 className="mb-3">Weitere Informationen</H2>
          <div className="flex flex-row gap-3 mb-8 items-end">
            <div>
              <label htmlFor="Telefonnummer" className={labelClassName}>
                Buchungszeichen/Kennziffer des Steuerbescheids (freiwillige
                Angabe){" "}
              </label>
              <Input value={"-"} readOnly />
            </div>
          </div>
        </div>
      )}
      <div className="flex flex-row justify-between">
        <Button
          variant="secondary"
          onPress={() => {
            router.push("/");
          }}
        >
          <Text>Zurück</Text>
        </Button>
        {data?.status === "ADDED" && (
          <Button
            onPress={async () => {
              await update.mutateAsync({
                ...data,
                status: "STARTED",
              });
            }}
            disabled={update.isPending}
          >
            <Text>Bearbeitung beginnen</Text>
          </Button>
        )}
        {data?.status === "STARTED" && (
          <Button
            onPress={async () => {
              await update.mutateAsync({
                ...data,
                status: "FINISHED",
              });
            }}
            disabled={update.isPending}
          >
            <Text>Fertigstellen</Text>
          </Button>
        )}
        {data?.status === "FINISHED" && (
          <Button
            onPress={async () => {
              await update.mutateAsync({
                ...data,
                status: "ARCHIVED",
              });
            }}
            disabled={update.isPending}
          >
            <Text>Archivieren</Text>
          </Button>
        )}
      </div>
    </div>
  );
}
