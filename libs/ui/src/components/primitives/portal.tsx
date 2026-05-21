import * as React from "react";

const DEFAULT_PORTAL_HOST = "INTERNAL_PRIMITIVE_DEFAULT_HOST_NAME";

type PortalMap = Map<string, React.ReactNode>;
type PortalHostMap = Map<string, PortalMap>;

const PortalContext = React.createContext<{
  map: PortalHostMap;
  updatePortal: (
    hostName: string,
    name: string,
    children: React.ReactNode,
  ) => void;
  removePortal: (hostName: string, name: string) => void;
}>({
  map: new Map<string, PortalMap>().set(
    DEFAULT_PORTAL_HOST,
    new Map<string, React.ReactNode>(),
  ),
  updatePortal: () => {},
  removePortal: () => {},
});

export function PortalProvider({ children }: { children: React.ReactNode }) {
  const [map, setMap] = React.useState<PortalHostMap>(
    new Map<string, PortalMap>().set(
      DEFAULT_PORTAL_HOST,
      new Map<string, React.ReactNode>(),
    ),
  );

  const updatePortal = (
    hostName: string,
    name: string,
    children: React.ReactNode,
  ) => {
    setMap((prev) => {
      const next = new Map(prev);
      const portal = next.get(hostName) ?? new Map<string, React.ReactNode>();
      portal.set(name, children);
      next.set(hostName, portal);
      return next;
    });
  };

  const removePortal = (hostName: string, name: string) => {
    setMap((prev) => {
      const next = new Map(prev);
      const portal = next.get(hostName) ?? new Map<string, React.ReactNode>();
      portal.delete(name);
      next.set(hostName, portal);
      return next;
    });
  };

  return (
    <PortalContext.Provider value={{ map, updatePortal, removePortal }}>
      {children}
    </PortalContext.Provider>
  );
}

export function PortalHost({ name = DEFAULT_PORTAL_HOST }: { name?: string }) {
  const { map } = React.useContext(PortalContext);
  const portalMap = map.get(name) ?? new Map<string, React.ReactNode>();
  if (portalMap.size === 0) return null;
  return <>{Array.from(portalMap.values())}</>;
}

export function Portal({
  name,
  hostName = DEFAULT_PORTAL_HOST,
  children,
}: {
  name: string;
  hostName?: string;
  children: React.ReactNode;
}) {
  const { updatePortal, removePortal } = React.useContext(PortalContext);

  // biome-ignore lint/correctness/useExhaustiveDependencies: <explanation>
  React.useEffect(() => {
    updatePortal(hostName, name, children);
  }, [hostName, name, children]);

  // biome-ignore lint/correctness/useExhaustiveDependencies: <explanation>
  React.useEffect(() => {
    return () => {
      removePortal(hostName, name);
    };
  }, [hostName, name]);

  return null;
}
