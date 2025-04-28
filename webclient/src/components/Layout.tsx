import React, { FC, JSX } from "react";
import { Outlet } from "react-router-dom";

import Navigation from "./Navigation";

const Layout: FC = (): JSX.Element => {
  return (
    <div className="w-full flex overflow-y-auto h-screen">
      <Navigation />
      <Outlet />
    </div>
  );
};

export default Layout;
