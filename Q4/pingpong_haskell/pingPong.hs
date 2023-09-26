import Control.Concurrent
import System.IO

ping :: Int -> MVar () -> MVar () -> Int -> IO ()
ping 0 _ _ _ = return ()
ping n pingSem pongSem count = do
    takeMVar pongSem --se tiver
    putStrLn $ "Toma aí o Ping número: " ++ show count
    putMVar pingSem ()
    ping (n - 1) pingSem pongSem (count + 1)

pong :: Int -> MVar () -> MVar () -> Int -> IO ()
pong 0 _ _ _ = return ()
pong n pingSem pongSem count = do
    putMVar pongSem ()
    takeMVar pingSem
    putStrLn $ "Eita! Recebi o Pong número: " ++ show count
    pong (n - 1) pingSem pongSem (count + 1)

main :: IO ()
main = do
    putStrLn "Quantas mensagens deseja enviar?"
    --recebe input do usuario
    n <- readLn

    pingSinal <- newEmptyMVar --cria MVar vazia como sinal de ping
    pongSinal <- newEmptyMVar --cria MVar vazio como sinal de pong

    --starta as threads e as determinadas funcoes
    let pingThread = ping n pingSinal pongSinal 1
    let pongThread = pong n pingSinal pongSinal 1

    forkIO pingThread
    forkIO pongThread

    threadDelay (n * 10000) 