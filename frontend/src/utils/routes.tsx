import { createBrowserRouter } from "react-router";
import { HomePage } from "../pages/HomePage";
import { CreateTestPage } from "../pages/CreateTestPage";
import { ResultsPage } from "../pages/ResultsPage";
import { HistoryPage } from "../pages/HistoryPage";
import { SettingsPage } from "../pages/SettingsPage";
import { DocumentationPage } from "../pages/DocumentationPage";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: HomePage,
  },
  {
    path: "/create",
    Component: CreateTestPage,
  },
  {
    path: "/results/:testId",
    Component: ResultsPage,
  },
  {
    path: "/history",
    Component: HistoryPage,
  },
  {
    path: "/settings",
    Component: SettingsPage,
  },
  {
    path: "/docs",
    Component: DocumentationPage,
  },
]);
