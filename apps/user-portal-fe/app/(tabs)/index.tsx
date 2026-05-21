import { DashboardCarousel } from "@/components/dashboard-carousel";
import { DynamicIcon, statusMap, useGetDogApplications } from "@repo/services";
import {
  Badge,
  Button,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  H2,
  LoadingIndicator,
  Logo,
  Text,
  useDisclosure,
} from "@repo/ui";
import { Pressable, ScrollView, TextInput, View } from "react-native";

export default function DashboardScreen() {
  const { data, isLoading } = useGetDogApplications();

  const showApplications = (isLoading || (data && data.length > 0)) ?? true;

  const carouselData = [
    ...(data?.map((item) => ({
      id: item.application.id,
      icon: item.application.service.icon,
      title: item.application.service.name,
      status: `Status: ${statusMap[item.application.status]}`,
    })) ?? []),
  ];

  const appointments = [
    {
      date: "Montag, den 10. Juni 2024",
      time: "14:00 Uhr",
      office: " im Bürgerbüro",
      room: "Raum 209",
      address: "Wilhelmstraße 9, 71638 Ludwigsburg",
    },
    {
      date: "Mittwoch, den 12. Juni 2024",
      time: "10:30",
      office: " im Standesamt",
      room: "Raum 301",
      address: "Marktplatz 1, 71638 Ludwigsburg",
    },
    {
      date: "Freitag, den 14. Juni 2024",
      time: "09:15 Uhr",
      office: " im Rathaus",
      room: "Raum 105",
      address: "Körnerstraße 14, 71638 Ludwigsburg",
    },
  ];

  const invoices = [
    {
      title: "Grundsteuerbescheid 2024",
      type: "PROPERTY",
    },
    {
      title: 'Rechnung zum Service "Ersatzmarke beantragen"',
      type: "DOG",
      status: "offen",
      amount: 5,
      date: "22.06.2024",
    },
  ];

  return (
    <ScrollView>
      <View className="flex py-6 px-4">
        <View className="flex flex-col items-center mb-4">
          <Logo />
        </View>

        <Text className="text-white mb-7 text-center text-2xl">
          Willkommen,{" "}
          <Text className="font-bold text-bold text-2xl">Max Mustermann!</Text>
        </Text>
        <View className="mb-7 h-[2px] bg-white w-28 mx-auto" />

        <H2 className="mb-2 text-white text-2xl">Dokumenteneingang</H2>

        <View className="w-full mb-7">
          <DashboardCarousel
            data={[...invoices]}
            renderItem={(item) => (
              <>
                {item.type === "PROPERTY" && (
                  <>
                    <Badge className="absolute top-0 right-0" variant="default">
                      <Text>neu</Text>
                    </Badge>
                    <CardHeader>
                      <CardTitle>Grundsteuerbescheid 2024</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <View className=" mx-auto flex flex-column space-x-2">
                        <Button
                          variant="outline"
                          onPress={() => console.log("Open pressed")}
                        >
                          <Text>Öffnen</Text>
                        </Button>
                      </View>
                    </CardContent>
                  </>
                )}

                {item.type === "DOG" && (
                  <>
                    <CardHeader>
                      <CardTitle>
                        Rechnung zum Service "Ersatzmarke beantragen"
                      </CardTitle>
                    </CardHeader>
                    <CardContent>
                      <Text>Fälligkeitsdatum: {item.date}</Text>
                      <Text>Rechnungsbetrag: {item.amount} €</Text>
                      <Text>Status: {item.status}</Text>
                    </CardContent>
                    <CardFooter>
                      <Button
                        onPress={() => console.log("Open pressed")}
                        variant="outline"
                      >
                        <Text>Öffnen</Text>
                      </Button>
                    </CardFooter>
                  </>
                )}
              </>
            )}
          />
        </View>

        {showApplications && (
          <H2 className="mb-2 text-white text-2xl">Ihre Anträge</H2>
        )}

        {showApplications && (
          <View className="w-full mb-10">
            {isLoading && <LoadingIndicator />}
            {data && data.length > 0 && (
              <DashboardCarousel
                data={carouselData}
                renderItem={(item) => {
                  // biome-ignore lint/correctness/useHookAtTopLevel: this is a component function
                  const [isOpen, { open, close }] = useDisclosure(false);
                  return (
                    <>
                      <CardHeader className="flex flex-col items-center gap-2">
                        <DynamicIcon
                          name={item.icon}
                          color="#1B6E98"
                          size={36}
                          className="shrink-0"
                        />
                        <CardTitle className="text-primary text-center">
                          {item.title}
                        </CardTitle>
                      </CardHeader>
                      <CardContent>
                        <Text className="text-center">
                          Antrags-ID: {item.id}
                        </Text>
                        <Pressable onPress={open}>
                          <Text className="text-center">{item.status}</Text>
                        </Pressable>
                      </CardContent>
                      <Dialog
                        open={isOpen}
                        onOpenChange={(value) => (value ? open() : close())}
                      >
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>
                              <Text className="font-bold">
                                Dienstbezeichnung:
                              </Text>
                              <Text>Hundesteuer - Hund anmelden</Text>
                            </DialogTitle>
                          </DialogHeader>
                          <View>
                            <View className="flex flex-row gap-4 mb-6">
                              <Text className="font-bold">Status:</Text>
                              <Text>Rückfrage vorhanden</Text>
                            </View>
                            <View className="mb-2">
                              <Text className="font-bold">Nachricht</Text>
                              <Text>
                                von Ihrem/r Sachbearbeiter/in: Maria Mustermann
                              </Text>
                            </View>
                            <TextInput
                              multiline
                              numberOfLines={4}
                              readOnly
                              inputMode="text"
                              className="border rounded mb-8"
                            />
                            <View className="flex flex-col gap-4">
                              <Button>
                                <Text>Dokument/e herunterladen</Text>
                              </Button>
                              <Button>
                                <Text>Informationen einreichen</Text>
                              </Button>
                            </View>
                          </View>
                        </DialogContent>
                      </Dialog>
                    </>
                  );
                }}
              />
            )}
          </View>
        )}

        <H2 className="mb-2 text-white text-2xl">Ihre Termine</H2>

        <View className="w-full">
          <DashboardCarousel
            data={appointments}
            renderItem={(item) => (
              <>
                <CardHeader>
                  <CardTitle>Personalausweisbeantragung</CardTitle>
                </CardHeader>
                <CardContent>
                  <Text>
                    Ihr nächster Termin zur Personalausweisbeantragung ist am{" "}
                    {item.date}, um {item.time}
                    {item.office}, {item.room} {item.address}
                  </Text>
                </CardContent>
              </>
            )}
          />
        </View>
      </View>
    </ScrollView>
  );
}
