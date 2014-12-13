# PANic

PANic is a Clojure command line tool implementing a partially known plaintext attack against unsalted SHA1 hashed primary account numbers (PAN's). This attack assumes the attacker chooses which [IIN (Issuer Identification Number)](https://en.wikipedia.org/wiki/Bank_card_number#Issuer_identification_number_.28IIN.29) to target, thereby greatly reducing the number of hashes that needs to be computed and compared.

This tool has been created primarily as a learning exercise, but also to demonstrate how easy it is to recover PAN's from SHA1 hashes when you can make assumptions about the card issuer. No adwanced techniques (rainbow tables, GPU etc) are used - which should only emphasize that this is a highly practical attack.

## Usage

```
~/d/p/panic ❯❯❯ java -jar target/panic-0.1.0-SNAPSHOT-standalone.jar
PANic. Tool for bruteforcing of primary account number (PAN) SHA1 hashes.

Usage: java <panic jar> [options]

Options:
  -i, --iin IIN              IIN range
  -t, --nthreads THREADS  4  Number of threads
  -f, --file FILE            Input file containing SHA1 hashes
  -h, --help

~/d/p/panic ❯❯❯
```

## Example

In this example, 100000 PAN's with the IIN '112233' are bruteforced.

```
~/d/p/panic ❯❯❯ time java -jar target/panic-0.1.0-SNAPSHOT-standalone.jar -i 112233 -t 4 -f resources/testdata
[000001/100000] 285031c26c5634be0a6e48104b190d7798a3fafb -> 1122332500106941
[000002/100000] de1b87a100d0eefd880e01107677e460245d863c -> 1122337500099079
[000003/100000] 8c145b9231788f636fb8d13df9ef7aa1d7bf223e -> 1122330000142465
[000004/100000] 8b5ebf5e9acab4d735c4fb761c64a368c82c28a8 -> 1122332500176829
[000005/100000] 7bb502c3249f801bba8464fc86c74e8dd17476ff -> 1122330000186710
[000006/100000] 5cab191ce34f6c16dedc7a32eb72cfa0c0561907 -> 1122332500180128
[000007/100000] d5c926b8f7ee1ac25977f5e1323c92ade6ebe35d -> 1122330000223257
[000008/100000] 6284815431e9d94e0dfbef8b164e4d970cbc6944 -> 1122337500232704
[000009/100000] 77c5dfa60158000d4e5059d4912a6ab7830996ae -> 1122335000265463
[000010/100000] 6510a78fc9f1f9059a86878ebf2fd41846a2b145 -> 1122332500322134
<...snip...>
[099991/100000] 365d6433cc8c0c972bd7f02e81b18a0330ea68ed -> 1122334999455136
[099992/100000] dd61d14253b331584893dfd2711d984ba0190dac -> 1122334999508488
[099993/100000] e2b40548a2c072369d6c82b7dc3c3eccb2de604a -> 1122334999643988
[099994/100000] 315de634a7aee69e5f0d98ee4e728b7b805ce73b -> 1122339999662227
[099995/100000] 40f754b2ed30c1a60bc4ab1d6f733210511b944f -> 1122339999680872
[099996/100000] 5a70ecc47dc5cb30e620fbdfa55ad1b22b2f3f2f -> 1122334999728425
[099997/100000] bd23cb06407f1290f9c1bd79e6bf7b81a1479194 -> 1122339999754081
[099998/100000] 73aa188f4eb85c68381bb4ece45e4613e9b5e721 -> 1122334999766229
[099999/100000] 122acc434d08843e282b1fd9fddd57ab177358fb -> 1122339999784658
[100000/100000] 49b318974cea70143322606aca4336f0b447e594 -> 1122334999913524
java -jar target/panic-0.1.0-SNAPSHOT-standalone.jar -i 112233 -t 4 -f   4594.85s user 22.97s system 364% cpu 21:05.31 total

```

## License

Copyright © 2014 Gunnar Helgason

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
