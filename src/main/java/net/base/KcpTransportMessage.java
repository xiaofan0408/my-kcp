package net.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KcpTransportMessage {

   private byte[] message;


   public ByteBuf toByteBuf(){
      if (message == null) {
         message = new byte[0];
      }
      return Unpooled.wrappedBuffer(message);
   }

}
