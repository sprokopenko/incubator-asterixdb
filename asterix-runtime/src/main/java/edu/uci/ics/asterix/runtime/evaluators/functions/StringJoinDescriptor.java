package edu.uci.ics.asterix.runtime.evaluators.functions;

import edu.uci.ics.asterix.common.exceptions.AsterixException;
import edu.uci.ics.asterix.common.functions.FunctionConstants;
import edu.uci.ics.asterix.dataflow.data.nontagged.serde.AOrderedListSerializerDeserializer;
import edu.uci.ics.asterix.formats.nontagged.AqlSerializerDeserializerProvider;
import edu.uci.ics.asterix.om.base.ANull;
import edu.uci.ics.asterix.om.functions.IFunctionDescriptor;
import edu.uci.ics.asterix.om.functions.IFunctionDescriptorFactory;
import edu.uci.ics.asterix.om.types.ATypeTag;
import edu.uci.ics.asterix.om.types.BuiltinType;
import edu.uci.ics.asterix.om.types.EnumDeserializer;
import edu.uci.ics.asterix.runtime.evaluators.base.AbstractScalarFunctionDynamicDescriptor;
import edu.uci.ics.hyracks.algebricks.core.algebra.functions.FunctionIdentifier;
import edu.uci.ics.hyracks.algebricks.common.exceptions.AlgebricksException;
import edu.uci.ics.hyracks.algebricks.common.exceptions.NotImplementedException;
import edu.uci.ics.hyracks.algebricks.runtime.base.ICopyEvaluator;
import edu.uci.ics.hyracks.algebricks.runtime.base.ICopyEvaluatorFactory;
import edu.uci.ics.hyracks.api.dataflow.value.ISerializerDeserializer;
import edu.uci.ics.hyracks.data.std.primitive.UTF8StringPointable;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.ArrayBackedValueStorage;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.IDataOutputProvider;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.IFrameTupleReference;
import edu.uci.ics.hyracks.dataflow.common.data.util.StringUtils;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Xiaoyu Ma
 */
public class StringJoinDescriptor extends AbstractScalarFunctionDynamicDescriptor {

    private static final long serialVersionUID = 1L;
    private final static FunctionIdentifier FID = new FunctionIdentifier(FunctionConstants.ASTERIX_NS, "string-join", 2,
            true);
    public static final IFunctionDescriptorFactory FACTORY = new IFunctionDescriptorFactory() {
        public IFunctionDescriptor createFunctionDescriptor() {
            return new StringJoinDescriptor();
        }
    };    
    private final static byte SER_STRING_TYPE_TAG = ATypeTag.STRING.serialize();
    private final static byte SER_NULL_TYPE_TAG = ATypeTag.NULL.serialize();
    private final static byte SER_ORDEREDLIST_TYPE_TAG = ATypeTag.ORDEREDLIST.serialize();
    private final byte stringTypeTag = ATypeTag.STRING.serialize();    

    @Override
    public ICopyEvaluatorFactory createEvaluatorFactory(final ICopyEvaluatorFactory[] args) {
        return new ICopyEvaluatorFactory() {

            private static final long serialVersionUID = 1L;        

            @Override
            public ICopyEvaluator createEvaluator(final IDataOutputProvider output) throws AlgebricksException {
                return new ICopyEvaluator() {

                    private DataOutput out = output.getDataOutput();
                    private ICopyEvaluatorFactory listEvalFactory = args[0];
                    private ICopyEvaluatorFactory sepEvalFactory = args[1];                    
                    private ArrayBackedValueStorage outInputList = new ArrayBackedValueStorage();
                    private ArrayBackedValueStorage outInputSep = new ArrayBackedValueStorage();                    
                    private ICopyEvaluator evalList = listEvalFactory.createEvaluator(outInputList);
                    private ICopyEvaluator evalSep = sepEvalFactory.createEvaluator(outInputSep);                    
                    @SuppressWarnings("unchecked")
                    private ISerializerDeserializer<ANull> nullSerde = AqlSerializerDeserializerProvider.INSTANCE.getSerializerDeserializer(BuiltinType.ANULL);           

                    @Override
                    public void evaluate(IFrameTupleReference tuple) throws AlgebricksException {
                        try {
                            outInputList.reset();
                            evalList.evaluate(tuple);
                            byte[] serOrderedList = outInputList.getByteArray();
                            
                            outInputSep.reset();
                            evalSep.evaluate(tuple);                            
                            byte[] serSep = outInputSep.getByteArray();
                            boolean noSep = false;
                            
                            if (serOrderedList[0] == SER_NULL_TYPE_TAG) {
                                nullSerde.serialize(ANull.NULL, out);
                                return;
                            }
                            if (serOrderedList[0] != SER_ORDEREDLIST_TYPE_TAG) {
                                throw new AlgebricksException("Expects String List."
                                        + EnumDeserializer.ATYPETAGDESERIALIZER.deserialize(serOrderedList[0]));
                            }
                            
                            if (serSep[0] == SER_NULL_TYPE_TAG) {
                                noSep = true;
                            }
                            if (serSep[0] != SER_STRING_TYPE_TAG) {
                                throw new AlgebricksException("Expects String as Seperator."
                                        + EnumDeserializer.ATYPETAGDESERIALIZER.deserialize(serOrderedList[0]));
                            }           
                            
                            int size = AOrderedListSerializerDeserializer.getNumberOfItems(serOrderedList);
                            try {
                                // calculate length first
                                int utf_8_len = 0;
                                int sep_len = UTF8StringPointable.getUTFLength(serSep, 1);
                                
                                for (int i = 0; i < size; i++) {
                                    int itemOffset = AOrderedListSerializerDeserializer.getItemOffset(serOrderedList, i);                                                                        
                                    int currentSize = UTF8StringPointable.getUTFLength(serOrderedList, itemOffset);  
                                    if(i != size - 1 && currentSize != 0) {
                                        utf_8_len += sep_len;
                                    }
                                    utf_8_len += currentSize;
                                }                               
                                out.writeByte(stringTypeTag);
                                StringUtils.writeUTF8Len(utf_8_len, out);                                                                 
                                for (int i = 0; i < size; i++) {
                                    int itemOffset = AOrderedListSerializerDeserializer.getItemOffset(serOrderedList, i);
                                    utf_8_len = UTF8StringPointable.getUTFLength(serOrderedList, itemOffset);        
                                    for(int j = 0; j < utf_8_len; j++) {
                                        out.writeByte(serOrderedList[2 + itemOffset + j]);
                                    }
                                    if(i == size - 1 || utf_8_len == 0)
                                        continue;
                                    for(int j = 0; j < sep_len; j++) {
                                        out.writeByte(serSep[3 + j]);
                                    }                                    
                                }                                
                            } catch (AsterixException ex) {
                                throw new AlgebricksException(ex);
                            }
                        } catch (IOException e1) {
                            throw new AlgebricksException(e1.getMessage());
                        }
                    }
                };
            }
        };
    }

    @Override
    public FunctionIdentifier getIdentifier() {
        return FID;
    }
}