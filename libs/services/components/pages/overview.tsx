import { Button, H2, Text } from "@repo/ui";
import { Link } from "expo-router";
import { View } from "react-native";
import type { Service } from "../../models/services";

type Props = {
  service: Service;
};

export function Overview({ service }: Props) {
  return (
    <View>
      <Text className="mb-3">
        Für jeden Hund, dessen Haltung im Stadtgebiet angezeigt wurde, wird eine
        Hundesteuermarke, die Eigentum der Stadt bleibt, ausgegeben. Diese
        gültige Steuermarke muss sichtbar befestigt werden.Bei Verlust der Marke
        ist die Stadtverwaltung, Abteilung Hundesteuer zu informieren.
      </Text>
      <H2>Voraussetzungen</H2>
      <Text className="mb-3">
        Die Steuermarke befindet sich nicht mehr am Hund und Sie können sie auch
        sonst nicht finden.
      </Text>
      <H2>Verfahrensablauf</H2>
      <Text className="mb-3">
        Sie beantragen die Ersatzmarke. Die Ersatzmarke wird Ihnen per Post
        zugeschickt.
      </Text>
      <H2>Fristen</H2>
      <Text className="mb-3">
        Sofort, nachdem Sie den Verlust der Marke bemerkt haben.
      </Text>
      <H2>Erforderliche Unterlagen</H2>
      <Text className="mb-3">Keine</Text>
      <H2>Kosten</H2>
      <Text className="mb-3">{service.cost} EUR</Text>
      <H2>Hinweise</H2>
      <Text className="mb-3">
        Fordern Sie keine Ersatzmarke an, kann das eine Ordnungswidrigkeit
        darstellen.
      </Text>
      <H2>Rechtsgrundlage</H2>
      <Text>
        § 9 Absatz 3 Kommunalabgabengesetz (KAG) (Gemeindesteuern) in Verbindung
        mit der jeweiligen Satzung Ihrer Gemeinde
      </Text>
      <View className="mt-7 w-full flex flex-row justify-center">
        <Link href={`./${service.slug}/1`} asChild>
          <Button className="shadow">
            <Text>Service starten</Text>
          </Button>
        </Link>
      </View>
    </View>
  );
}
