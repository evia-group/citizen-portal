import { Button, H2, Text } from "@repo/ui";
import { Link } from "expo-router";
import { Linking, Pressable, View } from "react-native";

export function Confirmation() {
  return (
    <View>
      <H2 className="mb-3">Vielen Dank für Ihren Antrag!</H2>

      <Text className="whitespace-pre-line mb-7">
        Ihr Anliegen wurde erfolgreich an die zuständige Behörde übermittelt.
        Wir werden Ihre Anfrage so schnell wie möglich bearbeiten und uns
        gegebenenfalls mit Ihnen in Verbindung setzen.
        {"\n"}
        {"\n"}
        Bitte beachten Sie, dass die Bearbeitungszeit je nach Art des Antrags
        variieren kann. Wir bitten um Ihre Geduld und stehen Ihnen bei Fragen
        gerne zur Verfügung.
        {"\n"}
        {"\n"}
        Für weitere Informationen oder Rückfragen können Sie sich gerne an
        unsere zuständige Behörde wenden:
        {"\n"}
        {"\n"}
        Obere Marktstraße 4 {"\n"}
        71634 Ludwigsburg
        {"\n"}
        {"\n"}
        oder per E-Mail an:{" "}
        <Pressable
          onPress={async () => {
            const mailAddress = "mailto:finanzen@ludwigsburg.de";
            const canOpen = await Linking.canOpenURL(mailAddress);
            if (canOpen) {
              Linking.openURL("mailto:finanzen@ludwigsburg.de");
            }
          }}
          className="underline"
        >
          <Text>finanzen@ludwigsburg.de</Text>
        </Pressable>
        {"\n"}
        {"\n"}
        Vielen Dank für Ihr Vertrauen in unsere Dienstleistungen!
      </Text>
      <View className="flex flex-row justify-end">
        <Link href="/" asChild>
          <Button>
            <Text>Weiter</Text>
          </Button>
        </Link>
      </View>
    </View>
  );
}
