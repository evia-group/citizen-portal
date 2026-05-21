import { Button, H2, H3, Text } from "@repo/ui";
import { Link } from "expo-router";
import { View } from "react-native";
import { CancelDialog } from "../cancel-dialog";
import { ProgressSteps } from "../progress";

export function DmsData() {
  return (
    <>
      <ProgressSteps count={4} step={3} title="Dokumente" />
      <View>
        <H2 className="mb-3">Dokumente</H2>

        <Text className="mb-3">
          Für diesen Antrag werden keine Dokumente angefordert.
        </Text>

        <H3>Laden Sie Ihre Dokumente hoch</H3>

        <View className="flex flex-row fle justify-between">
          <Link href="/dms/" asChild>
            <Button variant="secondary">
              <Text>Dokument aus DMS hochladen</Text>
            </Button>
          </Link>
        </View>

        <View className="flex flex-row justify-between mt-6">
          <Link href="./2" asChild>
            <Button variant="secondary">
              <Text>Zurück</Text>
            </Button>
          </Link>
          <CancelDialog />
          <Link href="./4" asChild>
            <Button>
              <Text>Weiter</Text>
            </Button>
          </Link>
        </View>
      </View>
    </>
  );
}
