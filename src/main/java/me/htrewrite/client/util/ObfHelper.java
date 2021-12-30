package me.htrewrite.client.util;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObfHelper
{
    public static boolean obfuscation;

    public static final String[] mainModel				= new String[] { "i", "field_77045_g", "mainModel" 				}; //RendererLivingEntity
    public static final String[] textureOffsetX 		= new String[] { "r", "field_78803_o", "textureOffsetX" 		}; //ModelRenderer
    public static final String[] textureOffsetY 		= new String[] { "s", "field_78813_p", "textureOffsetY" 		}; //ModelRenderer
    public static final String[] resourceDomain			= new String[] { "a", "field_110626_a", "resourceDomain"		}; //ResourceLocation
    public static final String[] resourcePath 			= new String[] { "b", "field_110625_b", "resourcePath" 			}; //ResourceLocation
    public static final String[] compiled 				= new String[] { "t", "field_78812_q", "compiled"	 			}; //ModelRenderer
    public static final String[] cameraZoom				= new String[] { "Y", "field_78503_V", "cameraZoom"				}; //EntityRenderer
    public static final String[] modelBipedMain			= new String[] { "f", "field_77109_a", "modelBipedMain"			}; //RenderPlayer
    public static final String[] isImmuneToFire			= new String[] { "ag","field_70178_ae", "isImmuneToFire"		}; //Entity
    public static final String[] isJumping 				= new String[] { "bd","field_70703_bu", "isJumping" 			}; //EntityLivingBase
    public static final String[] shadowSize				= new String[] { "d", "field_76989_e", "shadowSize"				}; //Render
    public static final String[] tagMap					= new String[] { "a", "field_74784_a", "tagMap"					}; //NBTTagCompound

    public static final String setSizeObf = "func_70105_a";
    public static final String setSizeDeobf = "setSize";

    public static final String updateEntityActionStateObf = "func_70626_be";
    public static final String updateEntityActionStateDeobf = "updateEntityActionState";

    public static final String getEntityTextureObf = "func_110775_a";
    public static final String getEntityTextureDeobf = "getEntityTexture";

    public static final String preRenderCallbackObf = "func_77041_b";
    public static final String preRenderCallbackDeobf = "preRenderCallback";

    public static final String pushOutOfBlocksObf = "func_70048_i";
    public static final String pushOutOfBlocksDeobf = "pushOutOfBlocks";

    public static final String getHurtSoundObf = "func_70621_aR";
    public static final String getHurtSoundDeobf = "getHurtSound";

    public static final String renderHandObf = "func_78476_b";
    public static final String renderHandDeobf = "renderHand";

    public static final String alterSquishAmountObf = "func_70808_l";

    public static Method setSizeMethod;
    public static Method pushOutOfBlocksMethod;

    public static void detectObfuscation()
    {
        obfuscation = true;
        try
        {
            Field[] fields = Class.forName("net.minecraft.world.World").getDeclaredFields();
            for(Field f : fields)
            {
                f.setAccessible(true);
                if(f.getName().equalsIgnoreCase("loadedEntityList"))
                {
                    obfuscation = false;
                    return;
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    public static void forceSetSize(Entity ent, float width, float height)
    {
        if(setSizeMethod == null)
        {
            try
            {
                Method m = EntityLivingBase.class.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.setSizeObf : ObfHelper.setSizeDeobf, float.class, float.class);
                setSizeMethod = m;
            }
            catch(NoSuchMethodException e)
            {
                ent.width = width;
                ent.height = height;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        if(setSizeMethod != null)
        {
            try
            {
                setSizeMethod.setAccessible(true);
                setSizeMethod.invoke(ent, width, height);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void forceUpdateEntityActionState(Class clz, EntityLivingBase ent)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.updateEntityActionStateObf : ObfHelper.updateEntityActionStateDeobf);
            m.setAccessible(true);
            m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityLivingBase.class)
            {
                forceUpdateEntityActionState(clz.getSuperclass(), ent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String invokeGetHurtSound(Class clz, EntityLivingBase ent)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.getHurtSoundObf : ObfHelper.getHurtSoundDeobf);
            m.setAccessible(true);
            return (String)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityLivingBase.class)
            {
                return invokeGetHurtSound(clz.getSuperclass(), ent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "damage.hit";
    }

    @SideOnly(Side.CLIENT)
    public static void invokeRenderHand(EntityRenderer renderer, float renderTick)
    {
        try
        {
            Method m = EntityRenderer.class.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.renderHandObf : ObfHelper.renderHandDeobf, float.class, int.class);
            m.setAccessible(true);
            m.invoke(renderer, renderTick, 0);
            return;
        }
        catch(NoSuchMethodException e)
        {
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void forceResquish(Class clz, EntitySlime slime)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.alterSquishAmountObf);
            m.setAccessible(true);
            m.invoke(slime);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntitySlime.class)
            {
                forceUpdateEntityActionState(clz.getSuperclass(), slime);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}