describe("Common Utils suite", function() {
    beforeAll(function() {
        fixture.setBase('test/fixtures/common')
    })
    beforeEach(function() {
        this.result = fixture.load('utilsTest.html');
    });
    afterEach(function() {
        fixture.cleanup()
    });

    it("test readData", function() {
        let options = CMP.utils.readData(fixture.el.firstElementChild, "image");
        expect(options.src).toBe(
            "https://s7g10.scene7.com/is/image/AEMSitesInternal/IMG_0006?qlt=82&wid=%7B.width%7D&ts=1666811582657&dpr=off");
        expect(options.widths).toBe("600,800,1000,1200,1600")
    });

    it("setup properties", function() {
        let options = CMP.utils.readData(fixture.el.firstElementChild, "image");
        let properties = CMP.utils.setupProperties(options, {
            "widths": {
                "default": [],
                "transform": function(value) {
                    var widths = [];
                    value.split(",").forEach(function(item) {
                        item = parseFloat(item);
                        if (!isNaN(item)) {
                            widths.push(item);
                        }
                    });
                    return widths;
                }
            }
        });

        expect(properties.widths).toEqual([600, 800, 1000, 1200, 1600])
    });
});