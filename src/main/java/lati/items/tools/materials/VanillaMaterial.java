package lati.items.tools.materials;

import lati.LatisTools;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.targets.FMLClientLaunchHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;

public class VanillaMaterial {

    private Tiers tier;
    private String name;
    private Color color;
    private BufferedImage image;

    public VanillaMaterial(Tiers baseTier, String name, Color color) {
        this.tier = baseTier;
        this.name = name;
        this.color = color;
        try {
            try {
                var readIn = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/latistoolsid/textures/items/better_tool.png")));
                ByteArrayInputStream bais = new ByteArrayInputStream(readIn.readLine().getBytes());
                BufferedImage im = ImageIO.read(bais);
                this.image = im;
            } catch (IOException e) {
                e.printStackTrace();
            }
            generateTexture(this.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public Tiers getTier() {
        return tier;
    }

    public Color getColor() {
        return color;
    }

    public static VanillaMaterial WOOD = new VanillaMaterial(Tiers.WOOD, "wood", new Color(107,81,31));
    public static VanillaMaterial STONE = new VanillaMaterial(Tiers.STONE, "stone", new Color(127,127,127));
    public static VanillaMaterial IRON = new VanillaMaterial(Tiers.IRON, "iron", new Color(193,193,193));
    public static VanillaMaterial GOLD = new VanillaMaterial(Tiers.GOLD, "gold", new Color(205,208,80));
    public static VanillaMaterial DIAMOND = new VanillaMaterial(Tiers.DIAMOND, "diamond", new Color(41,200,171));
    public static VanillaMaterial NETHERITE = new VanillaMaterial(Tiers.NETHERITE, "netherite", new Color(79,61,62));

    public static java.util.List<VanillaMaterial> vanillaMaterials = java.util.List.of(WOOD, STONE, IRON, GOLD, DIAMOND, NETHERITE);

    private void generateTexture(String name) throws IOException {

        float easeFactor = 0.5f;

        BufferedImage easedImage = easeColor(this.image, this.color, easeFactor);

        String outputFilename = "better_tool_" + name + ".png";

        File outputFile = new File(outputFilename);
        ImageIO.write(easedImage, "png", outputFile);
    }

    private static BufferedImage easeColor(BufferedImage image, Color targetColor, float easeFactor) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage easedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color originalColor = new Color(rgb);

                //Calc new
                int newRed = (int) (originalColor.getRed() * (1.0f - easeFactor) + targetColor.getRed() * easeFactor);
                int newGreen = (int) (originalColor.getGreen() * (1.0f - easeFactor) + targetColor.getGreen() * easeFactor);
                int newBlue = (int) (originalColor.getBlue() * (1.0f - easeFactor) + targetColor.getBlue() * easeFactor);

                // Clamp color values to 0-255
                newRed = Math.max(0, Math.min(255, newRed));
                newGreen = Math.max(0, Math.min(255, newGreen));
                newBlue = Math.max(0, Math.min(255, newBlue));

                // Create and set the eased color
                Color easedColor = new Color(newRed, newGreen, newBlue);
                easedImage.setRGB(x, y, easedColor.getRGB());
            }
        }
        return easedImage;
    }
}
