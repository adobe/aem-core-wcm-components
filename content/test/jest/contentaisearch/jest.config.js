module.exports = {
    testEnvironment: "jsdom",
    testEnvironmentOptions: {
        // AEM as a Cloud Service is HTTPS-only, and the cookie set by contentaisearch.js
        // now carries the Secure attribute - jsdom's default http://localhost origin would
        // silently drop such cookies, so the test origin is pinned to https here to match
        // production reality.
        url: "https://localhost/"
    },
    testMatch: ["**/*.test.js"]
};
