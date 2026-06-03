import {
  type Category,
  Details,
  type Domain,
  useServiceList,
} from "@repo/services";
import { Button, Input, LoadingIndicator, Text } from "@repo/ui";
import { Stack, useLocalSearchParams } from "expo-router";
import { openBrowserAsync } from "expo-web-browser";
import { useForm } from "react-hook-form";
import { Platform, ScrollView, View } from "react-native";
import { Header } from "@/components/header";
import { Item } from "./index";

export default function ServicesRest() {
  const { rest } = useLocalSearchParams<{ rest: string[] }>();
  const [domainSlug, categorySlug, serviceSlug, page] = rest;

  const { data, isLoading, isError, error } = useServiceList();

  if (data) {
    const domain = data.find((d) => d.slug === domainSlug);
    const category = domain?.categories?.find((c) => c.slug === categorySlug);
    const service = category?.services?.find((s) => s.slug === serviceSlug);

    return (
      <ScrollView className="py-5">
        {domain && !category && !service && <ServicesDomain domain={domain} />}
        {domain && category && !service && (
          <ServicesCategory domain={domain} category={category} />
        )}
        {domain &&
          category &&
          service &&
          service.slug === "replace-dog-tag" && (
            <Details service={service} page={page ? +page : undefined} />
          )}
        {domain && category && service && service.slug === "dog-tax" && (
          <View>
            <Stack.Screen
              options={{
                title: service.name,
              }}
            />
            <Text>Info</Text>
            <Button
              onPress={async () => {
                const url =
                  "https://www.service-bw.de/zufi/leistungen/6003845?plz=71636-71634-71642-71640-71638&ags=08118048";
                if (Platform.OS === "web") {
                  window.open(url, "_blank");
                } else {
                  await openBrowserAsync(url);
                }
              }}
            >
              <Text>Service starten</Text>
            </Button>
          </View>
        )}
      </ScrollView>
    );
  }

  return (
    <ScrollView className="py-5">
      {isLoading && <LoadingIndicator />}
      {isError && <Text>Error... {error.message}</Text>}
    </ScrollView>
  );
}

function ServicesDomain({ domain }: { domain: Domain }) {
  const { control, watch } = useForm({
    defaultValues: { search: "" },
  });
  const searchValue = watch("search");

  let categories = domain.categories;

  if (searchValue) {
    categories = categories.filter((category) =>
      category.title.toLowerCase().includes(searchValue.toLowerCase()),
    );
  }

  return (
    <View>
      <Stack.Screen
        options={{
          title: domain.title,
          header({ navigation, options }) {
            return (
              <Header
                onPress={() => navigation.goBack()}
                more={
                  <Input
                    placeholder="Kategorie suchen..."
                    name="search"
                    control={control}
                    aria-label="Kategorie suchen..."
                    className="mt-4"
                    type="search"
                  />
                }
              >
                {options.title}
              </Header>
            );
          },
          contentStyle: { backgroundColor: "white" },
        }}
      />
      <View className="py-5">
        {categories.map((category, index) => (
          <Item
            icon={category.icon}
            href={`/services/${domain.slug}/${category.slug}`}
            isFirst={index === 0}
            key={category.slug}
          >
            {category.title}
          </Item>
        ))}
      </View>
    </View>
  );
}

function ServicesCategory({
  domain,
  category,
}: {
  domain: Domain;
  category: Category;
}) {
  const { control, watch } = useForm({
    defaultValues: { search: "" },
  });
  const searchValue = watch("search");

  let services = category.services;

  if (searchValue) {
    services = services.filter((service) =>
      service.name.toLowerCase().includes(searchValue.toLowerCase()),
    );
  }

  return (
    <View>
      <Stack.Screen
        options={{
          title: category.title,
          header({ navigation, options }) {
            return (
              <Header
                onPress={() => navigation.goBack()}
                more={
                  <Input
                    placeholder="Dienst suchen..."
                    name="search"
                    control={control}
                    aria-label="Dienst suchen..."
                    className="mt-4"
                    type="search"
                  />
                }
              >
                {options.title}
              </Header>
            );
          },
          contentStyle: { backgroundColor: "white" },
        }}
      />
      <View className="py-5">
        {services.map((service, index) => (
          <Item
            icon={service.icon}
            href={`/services/${domain.slug}/${category.slug}/${service.slug}`}
            isFirst={index === 0}
            key={service.slug}
          >
            {service.name}
          </Item>
        ))}
      </View>
    </View>
  );
}
