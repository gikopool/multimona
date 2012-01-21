/**
 * Copyright 2012 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.multibit.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import junit.framework.TestCase;

import org.junit.Test;
import org.multibit.Constants;
import org.multibit.Localiser;
import org.multibit.controller.MultiBitController;
import org.multibit.file.FileHandler;
import org.multibit.model.MultiBitModel;
import org.multibit.model.PerWalletModelData;
import org.multibit.network.MultiBitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Wallet;

public class PrivateKeysHandlerTest extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(PrivateKeysHandlerTest.class);

    public static final String PRIVATE_KEYS_TESTDATA_DIRECTORY = "privateKeys";

    public static final String TEST1_WALLET_FILE = "test1.wallet";
    public static final String TEST1_PRIVATE_KEYS_FILE = "test1.key";
    public static final String EXPECTED_TEST1_PRIVATE_KEYS_FILE = "expectedTest1.key";

    @Test
    public void testLoadTest1() throws IOException {
        MultiBitController controller = new MultiBitController();
        
        Localiser localiser = new Localiser();
        MultiBitModel model = new MultiBitModel(controller);
        
        controller.setLocaliser(localiser);
        controller.setModel(model);   

        PrivateKeysHandler privateKeysHandler = new PrivateKeysHandler(NetworkParameters.prodNet());

        File directory = new File(".");
        String currentPath = directory.getAbsolutePath();

        String testDirectory = currentPath + File.separator + Constants.TESTDATA_DIRECTORY + File.separator
                + PRIVATE_KEYS_TESTDATA_DIRECTORY;
        String testWalletFile = testDirectory + File.separator + TEST1_WALLET_FILE;
        String testPrivateKeysFile = testDirectory + File.separator + TEST1_PRIVATE_KEYS_FILE;
        String expectedPrivateKeysFile = testDirectory + File.separator + EXPECTED_TEST1_PRIVATE_KEYS_FILE;

        // load up the test wallet
        FileHandler fileHandler = new FileHandler(controller);
        Wallet testWallet = fileHandler.loadFromFile(new File(testWalletFile)).getWallet();

        assertNotNull(testWallet);

        // write private keys file for wallet
        privateKeysHandler.exportPrivateKeys(new File(testPrivateKeysFile), testWallet);

        // read in the created private keys file and the expected one and
        // compare
        String expectedFileContents = readFile(new File(expectedPrivateKeysFile));
        String actualFileContents = readFile(new File(testPrivateKeysFile));

        assertEquals(expectedFileContents, actualFileContents);
    }

    private String readFile(File inputFile) throws IOException {

        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        String lineSeparator = System.getProperty("line.separator");
        try {
            reader = new BufferedReader(new FileReader(inputFile));

            String text = null;

            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
                contents.append(text).append(lineSeparator);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return contents.toString();
    }
}
